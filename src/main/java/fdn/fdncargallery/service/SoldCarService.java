package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.soldCar.BranchMonthlySalesDto;
import fdn.fdncargallery.dto.soldCar.CreateSoldCarRequestDto;
import fdn.fdncargallery.dto.soldCar.MonthlySalesDto;
import fdn.fdncargallery.dto.soldCar.MonthlySalesTotals;
import fdn.fdncargallery.dto.soldCar.SoldCarResponseDto;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.Customer;
import fdn.fdncargallery.entity.Manager;
import fdn.fdncargallery.entity.MonthlySalesSummary;
import fdn.fdncargallery.entity.SoldCar;
import fdn.fdncargallery.entity.StockItem;
import fdn.fdncargallery.enums.CarStatus;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.ISoldCarMapper;
import fdn.fdncargallery.repository.IBranchRepository;
import fdn.fdncargallery.repository.IEmployeeRepository;
import fdn.fdncargallery.repository.IMonthlySalesSummaryRepository;
import fdn.fdncargallery.repository.ISoldCarRepository;
import fdn.fdncargallery.service.interfaces.IBranchService;
import fdn.fdncargallery.service.interfaces.ICustomerService;
import fdn.fdncargallery.service.interfaces.IEmployeeService;
import fdn.fdncargallery.service.interfaces.IReservationService;
import fdn.fdncargallery.service.interfaces.ISoldCarService;
import fdn.fdncargallery.service.interfaces.IStockItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class SoldCarService implements ISoldCarService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final ISoldCarRepository soldCarRepository;
    private final IMonthlySalesSummaryRepository monthlySalesSummaryRepository;
    private final IEmployeeRepository employeeRepository;
    private final IBranchRepository branchRepository;
    private final ISoldCarMapper soldCarMapper;
    private final IStockItemService stockItemService;
    private final ICustomerService customerService;
    private final IReservationService reservationService;
    private final IEmployeeService employeeService;
    private final IBranchService branchService;
    private final SecurityService securityService;

    @Transactional
    @Override
    public SoldCarResponseDto createSoldCar(CreateSoldCarRequestDto requestDto) {

        StockItem stockItem = stockItemService.getStockItemEntityById(requestDto.getStockItemId());
        securityService.checkBranchAccess(stockItem.getBranch().getId());

        if (stockItem.getStatus() == CarStatus.IN_MAINTENANCE) {
            throw new BaseException(new ErrorMessage(MessageType.STOCK_ITEM_UNDER_MAINTENANCE_CANNOT_BE_SOLD, stockItem.getId().toString()));
        }

        if (stockItem.getStatus() != CarStatus.AVAILABLE && stockItem.getStatus() != CarStatus.RESERVED) {
            throw new BaseException(new ErrorMessage(MessageType.STOCK_ITEM_NOT_AVAILABLE_FOR_SALE,
                    "Yalnızca satışta (AVAILABLE) ya da rezerve (RESERVED) olan araç satılabilir. Mevcut durum: " + stockItem.getStatus()));
        }

        Customer customer = customerService.getCustomerEntityById(requestDto.getCustomerId());
        BaseEmployee seller = securityService.getCurrentEmployee();

        checkDiscountLimit(stockItem, requestDto.getSalePrice(), seller);

        // rezerve araç yalnızca rezervasyon sahibine satılır; süresi geçmiş rezervasyon kapatılır
        if (stockItem.getStatus() == CarStatus.RESERVED) {
            reservationService.convertForSale(stockItem, customer);
        }

        SoldCar soldCar = soldCarMapper.toEntity(requestDto);
        soldCar.setStockItem(stockItem);
        soldCar.setCustomer(customer);
        soldCar.setEmployee(seller);
        soldCar.setSaleDate(LocalDateTime.now());
        // komisyon oranı aracın şubesinden alınıp satışa kopyalanır: şubenin oranı sonradan değişse de bu satışın komisyonu değişmez
        soldCar.setCommissionRate(seller.commissionRateForSale(stockItem.getBranch()));

        // StockItem'daki @Version aynı aracın eşzamanlı iki kez satılmasını engeller
        stockItem.setStatus(CarStatus.SOLD);
        stockItem.setSoldAt(LocalDate.now());
        seller.recordSale();

        SoldCar saved = soldCarRepository.saveAndFlush(soldCar);

        log.info("Araç satıldı. id: {}, stockItemId: {}, müşteri id: {}, satış fiyatı: {}, liste fiyatı: {}, prim oranı: {}, personel id: {}",
                saved.getId(), stockItem.getId(), customer.getId(), saved.getSalePrice(), stockItem.getListPrice(),
                saved.getCommissionRate(), seller.getId());

        return soldCarMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public SoldCarResponseDto findSoldCarById(Long id) {

        SoldCar soldCar = getSoldCarEntityById(id);
        checkSoldCarAccess(soldCar);

        return soldCarMapper.toResponse(soldCar);
    }

    @Transactional
    @Override
    public List<SoldCarResponseDto> findAllSoldCars() {

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();

        List<SoldCar> soldCars;
        if (securityService.isSuperAdmin()) {
            soldCars = soldCarRepository.findAllByOrderBySaleDateDesc();
        } else if (currentEmployee.getRole() == Role.SALES_REP) {
            soldCars = soldCarRepository.findAllByEmployeeIdOrderBySaleDateDesc(currentEmployee.getId());
        } else {
            soldCars = soldCarRepository.findAllByStockItem_Branch_IdOrderBySaleDateDesc(securityService.getCurrentBranchId());
        }

        return soldCars.stream()
                .map(soldCarMapper::toResponse)
                .toList();
    }

    // kapanmış aylar ay kapanışı tablosundan, içinde bulunulan ay satış kayıtlarından okunur; iptal edilen satışlar sayılmaz.
    // müdür ve şube yöneticisi yalnızca kendi şubesindeki personelin raporunu, o şubede yaptığı satışlarla görür
    @Transactional
    @Override
    public List<MonthlySalesDto> findMonthlySalesByEmployee(Long employeeId) {

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();

        if (currentEmployee.getRole() == Role.SALES_REP) {
            if (!currentEmployee.getId().equals(employeeId)) {
                throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED,
                        "Sadece kendi satış geçmişinizi görebilirsiniz."));
            }
            return monthlySales(employeeId, null);
        }

        BaseEmployee employee = employeeService.getEmployeeEntityById(employeeId);

        if (securityService.isSuperAdmin()) {
            return monthlySales(employeeId, null);
        }

        securityService.checkBranchAccess(employee.getBranch().getId());
        return monthlySales(employeeId, securityService.getCurrentBranchId());
    }

    // şubenin bir aylık tablosu: o ay şubede satış yapan her personel için bir satır, en çok satan üstte.
    // kapanmış ay tablodan, içinde bulunulan ay satış kayıtlarından okunur; gelecek ay boş döner
    @Transactional
    @Override
    public List<BranchMonthlySalesDto> findMonthlySalesByBranch(Long branchId, int year, int month) {

        if (month < 1 || month > 12) {
            throw new BaseException(new ErrorMessage(MessageType.VALIDATION_ERROR, "Ay 1 ile 12 arasında olmalıdır."));
        }

        securityService.checkBranchAccess(branchId);
        branchService.getBranchEntityById(branchId);

        YearMonth salesMonth = YearMonth.of(year, month);
        YearMonth currentMonth = YearMonth.now();

        List<MonthlySalesSummary> summaries = new ArrayList<>();
        if (salesMonth.isBefore(currentMonth)) {
            summaries = monthlySalesSummaryRepository.findAllByBranchIdAndSalesYearAndSalesMonth(branchId, year, month);
        } else if (salesMonth.equals(currentMonth)) {
            for (MonthlySalesSummary summary : buildSummaries(currentMonth)) {
                if (summary.getBranch().getId().equals(branchId)) {
                    summaries.add(summary);
                }
            }
        }

        List<BranchMonthlySalesDto> result = new ArrayList<>();
        for (MonthlySalesSummary summary : summaries) {
            BaseEmployee employee = summary.getEmployee();
            result.add(new BranchMonthlySalesDto(employee.getId(), employee.getFullName(), employee.getRole(),
                    summary.getSaleCount(), summary.getTotalSales(), summary.getTotalCommission(), summary.getTargetBonus()));
        }
        result.sort(Comparator.comparing(BranchMonthlySalesDto::saleCount).reversed());
        return result;
    }

    // ay kapanışı: her ayın 1'inde ve uygulama her açıldığında kapanmamış geçmiş aylar dondurulur, içinde bulunulan ay kapanmaz.
    // kapanmış aya bir daha dokunulmaz; sonradan hedef değişse, temsilci şube değiştirse ya da satış iade edilse de kayıt aynı kalır
    @Scheduled(cron = "0 0 0 1 * *", zone = "Europe/Istanbul")
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void closePastMonths() {

        Optional<SoldCar> firstSale = soldCarRepository.findFirstByOrderBySaleDateAsc();
        if (firstSale.isEmpty()) {
            return;
        }

        YearMonth currentMonth = YearMonth.now();
        for (YearMonth month = YearMonth.from(firstSale.get().getSaleDate()); month.isBefore(currentMonth); month = month.plusMonths(1)) {
            if (monthlySalesSummaryRepository.existsBySalesYearAndSalesMonth(month.getYear(), month.getMonthValue())) {
                continue;
            }
            List<MonthlySalesSummary> summaries = buildSummaries(month);
            if (!summaries.isEmpty()) {
                monthlySalesSummaryRepository.saveAll(summaries);
                log.info("Ay kapandı. ay: {}, kayıt sayısı: {}", month, summaries.size());
            }
        }
    }

    // iade: araç satışa döner, temsilcinin aylık sayacı geri alınır; satışa dönüşen rezervasyon yeniden açılmaz
    @Transactional
    @Override
    public void deleteSoldCar(Long id) {

        SoldCar soldCar = getSoldCarEntityById(id);
        securityService.checkBranchAccess(soldCar.getStockItem().getBranch().getId());

        StockItem stockItem = soldCar.getStockItem();
        stockItem.setStatus(CarStatus.AVAILABLE);
        stockItem.setSoldAt(null);

        soldCar.getEmployee().revertSale(soldCar.getSaleDate());

        soldCar.softDelete(securityService.getCurrentEmployee());
        soldCarRepository.saveAndFlush(soldCar);

        log.info("Satış iptal edildi, araç satışa döndü. id: {}, stockItemId: {}", id, stockItem.getId());
    }

    // bir ayda birden fazla şubede satış varsa şube kayıtları toplanır; branchId verilirse yalnızca o şubedeki satışlar sayılır
    private List<MonthlySalesDto> monthlySales(Long employeeId, Long branchId) {

        // kapanmış ayların kayıtları tablodan gelir; içinde bulunulan ayınki o an hesaplanıp eklenir
        List<MonthlySalesSummary> summaries = new ArrayList<>(monthlySalesSummaryRepository.findAllByEmployeeId(employeeId));
        for (MonthlySalesSummary summary : buildSummaries(YearMonth.now())) {
            if (summary.getEmployee().getId().equals(employeeId)) {
                summaries.add(summary);
            }
        }

        // kayıtlar aylarına göre gruplanır; TreeMap ayları yeniden eskiye sıralı tutar
        Map<YearMonth, List<MonthlySalesSummary>> summariesByMonth = new TreeMap<>(Comparator.reverseOrder());
        for (MonthlySalesSummary summary : summaries) {
            if (branchId != null && !summary.getBranch().getId().equals(branchId)) {
                continue;
            }
            YearMonth month = YearMonth.of(summary.getSalesYear(), summary.getSalesMonth());
            if (!summariesByMonth.containsKey(month)) {
                summariesByMonth.put(month, new ArrayList<>());
            }
            summariesByMonth.get(month).add(summary);
        }

        List<MonthlySalesDto> result = new ArrayList<>();
        for (YearMonth month : summariesByMonth.keySet()) {
            result.add(toMonthlySalesDto(month, summariesByMonth.get(month)));
        }
        return result;
    }

    private MonthlySalesDto toMonthlySalesDto(YearMonth month, List<MonthlySalesSummary> summaries) {
        return new MonthlySalesDto(
                month.getYear(),
                month.getMonthValue(),
                summaries.stream().mapToLong(MonthlySalesSummary::getSaleCount).sum(),
                summaries.stream().map(MonthlySalesSummary::getTotalSales).reduce(BigDecimal.ZERO, BigDecimal::add),
                summaries.stream().map(MonthlySalesSummary::getTotalCommission).reduce(BigDecimal.ZERO, BigDecimal::add),
                summaries.stream().map(MonthlySalesSummary::getTargetBonus).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    // ayın satışları personel + şube bazında toplanır; şubenin o anki hedefi ve tutarı kayda yazılır
    private List<MonthlySalesSummary> buildSummaries(YearMonth month) {

        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.plusMonths(1).atDay(1).atStartOfDay();

        return soldCarRepository.findMonthlyTotals(start, end).stream()
                .map(totals -> {
                    MonthlySalesSummary summary = new MonthlySalesSummary();
                    summary.setEmployee(employeeRepository.getReferenceById(totals.employeeId()));
                    summary.setBranch(branchRepository.getReferenceById(totals.branchId()));
                    summary.setSalesYear(month.getYear());
                    summary.setSalesMonth(month.getMonthValue());
                    summary.setSaleCount(totals.saleCount());
                    summary.setTotalSales(totals.totalSales());
                    summary.setTotalCommission(totals.totalCommission());
                    summary.setMonthlySalesTarget(totals.monthlySalesTarget());
                    summary.setTargetBonusPerCar(totals.targetBonusPerCar());
                    summary.setTargetBonus(targetBonusFor(totals));
                    return summary;
                })
                .toList();
    }

    // hedef primi yalnızca temsilciye, şube hedefini aşan her araç için verilir; hedefi girilmemiş şubede verilmez
    private BigDecimal targetBonusFor(MonthlySalesTotals totals) {

        if (totals.employeeRole() != Role.SALES_REP
                || totals.monthlySalesTarget() == null
                || totals.targetBonusPerCar() == null) {
            return BigDecimal.ZERO;
        }

        long carsAboveTarget = Math.max(0, totals.saleCount() - totals.monthlySalesTarget());
        return totals.targetBonusPerCar().multiply(BigDecimal.valueOf(carsAboveTarget));
    }

    private SoldCar getSoldCarEntityById(Long id) {
        return soldCarRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.SALE_RECORD_NOT_FOUND, id.toString())));
    }

    // şube kontrolüne ek olarak: satış temsilcisi yalnızca kendi yaptığı satışa erişebilir
    private void checkSoldCarAccess(SoldCar soldCar) {

        securityService.checkBranchAccess(soldCar.getStockItem().getBranch().getId());

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();
        if (currentEmployee.getRole() == Role.SALES_REP && !soldCar.getEmployee().getId().equals(currentEmployee.getId())) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED,
                    "Sadece kendi yaptığınız satışlara erişebilirsiniz."));
        }
    }

    // indirim, aracın şubesindeki müdürün limitini aşamaz; şube yöneticisi ve süper admin muaf
    private void checkDiscountLimit(StockItem stockItem, BigDecimal salePrice, BaseEmployee seller) {

        if (seller.getRole() == Role.BRANCH_ADMIN || seller.getRole() == Role.SUPER_ADMIN) {
            return;
        }

        BigDecimal listPrice = stockItem.getListPrice();
        BigDecimal discount = listPrice.subtract(salePrice);
        if (discount.signum() <= 0) {
            return;
        }

        Manager manager = stockItem.getBranch().getManager();
        BigDecimal maxRate = manager != null && manager.getMaxDiscountRate() != null
                ? manager.getMaxDiscountRate()
                : BigDecimal.ZERO;

        // oranlar yüzde olarak tutulur; bölme yapmadan karşılaştırılır ki yuvarlama sınırı esnetmesin
        if (discount.multiply(HUNDRED).compareTo(maxRate.multiply(listPrice)) > 0) {
            BigDecimal requestedRate = discount.multiply(HUNDRED).divide(listPrice, 2, RoundingMode.HALF_UP);
            throw new BaseException(new ErrorMessage(MessageType.DISCOUNT_LIMIT_EXCEEDED,
                    manager == null
                            ? "Şubede müdür olmadığı için indirim yapılamaz."
                            : "İstenen indirim %" + requestedRate + ", şube sınırı %" + maxRate + "."));
        }
    }
}
