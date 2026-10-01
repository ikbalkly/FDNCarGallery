package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.soldCar.CreateSoldCarRequestDto;
import fdn.fdncargallery.dto.soldCar.MonthlySalesDto;
import fdn.fdncargallery.dto.soldCar.SoldCarResponseDto;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.Customer;
import fdn.fdncargallery.entity.Manager;
import fdn.fdncargallery.entity.SoldCar;
import fdn.fdncargallery.entity.StockItem;
import fdn.fdncargallery.enums.CarStatus;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.ISoldCarMapper;
import fdn.fdncargallery.repository.ISoldCarRepository;
import fdn.fdncargallery.service.interfaces.ICustomerService;
import fdn.fdncargallery.service.interfaces.IEmployeeService;
import fdn.fdncargallery.service.interfaces.IReservationService;
import fdn.fdncargallery.service.interfaces.ISoldCarService;
import fdn.fdncargallery.service.interfaces.IStockItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SoldCarService implements ISoldCarService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final ISoldCarRepository soldCarRepository;
    private final ISoldCarMapper soldCarMapper;
    private final IStockItemService stockItemService;
    private final ICustomerService customerService;
    private final IReservationService reservationService;
    private final IEmployeeService employeeService;
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
        // prim oranı satış anında sabitlenir: temsilcinin oranı sonradan değişse de bu satışın primi değişmez
        soldCar.setCommissionRate(seller.commissionRateForSale());

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

    // geçmiş satış kayıtlarından hesaplanır, iptal edilen satışlar sayılmaz;
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
            return soldCarRepository.findMonthlySalesByEmployeeId(employeeId);
        }

        BaseEmployee employee = employeeService.getEmployeeEntityById(employeeId);

        if (securityService.isSuperAdmin()) {
            return soldCarRepository.findMonthlySalesByEmployeeId(employeeId);
        }

        securityService.checkBranchAccess(employee.getBranch().getId());
        return soldCarRepository.findMonthlySalesByEmployeeIdAndBranchId(employeeId, securityService.getCurrentBranchId());
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
