package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.carMaintenance.CarMaintenanceResponseDto;
import fdn.fdncargallery.dto.carMaintenance.CompleteCarMaintenanceRequestDto;
import fdn.fdncargallery.dto.carMaintenance.CreateCarMaintenanceRequestDto;
import fdn.fdncargallery.dto.carMaintenance.UpdateCarMaintenanceRequestDto;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.CarMaintenance;
import fdn.fdncargallery.entity.StockItem;
import fdn.fdncargallery.enums.CarStatus;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.ICarMaintenanceMapper;
import fdn.fdncargallery.repository.ICarMaintenanceRepository;
import fdn.fdncargallery.service.interfaces.ICarMaintenanceService;
import fdn.fdncargallery.service.interfaces.IStockItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarMaintenanceService implements ICarMaintenanceService {

    private final ICarMaintenanceRepository carMaintenanceRepository;
    private final ICarMaintenanceMapper carMaintenanceMapper;
    private final IStockItemService stockItemService;
    private final SecurityService securityService;

    @Transactional
    @Override
    public CarMaintenanceResponseDto createCarMaintenance(CreateCarMaintenanceRequestDto requestDto) {

        StockItem stockItem = stockItemService.getStockItemEntityById(requestDto.getStockItemId());
        securityService.checkBranchAccess(stockItem.getBranch().getId());

        if (stockItem.getStatus() == CarStatus.IN_MAINTENANCE) {
            throw new BaseException(new ErrorMessage(MessageType.STOCK_ITEM_ALREADY_IN_MAINTENANCE, stockItem.getId().toString()));
        }

        // satılmış ya da rezerve araç bakıma gönderilemez
        if (stockItem.getStatus() != CarStatus.AVAILABLE) {
            throw new BaseException(new ErrorMessage(MessageType.STOCK_ITEM_NOT_AVAILABLE_FOR_SALE,
                    "Yalnızca satışta (AVAILABLE) olan bir araç bakıma gönderilebilir. Mevcut durum: " + stockItem.getStatus()));
        }

        checkNotBeforeStartDate(requestDto.getStartDate(), requestDto.getExpectedEndDate(), "Beklenen bitiş tarihi");

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();

        CarMaintenance maintenance = carMaintenanceMapper.toEntity(requestDto);
        maintenance.setStockItem(stockItem);
        maintenance.setEmployee(currentEmployee);

        // StockItem'daki @Version aynı araca eşzamanlı iki bakım açılmasını engeller
        stockItem.setStatus(CarStatus.IN_MAINTENANCE);

        CarMaintenance saved = carMaintenanceRepository.saveAndFlush(maintenance);

        log.info("Bakım kaydı açıldı. id: {}, stockItemId: {}, tip: {}, personel id: {}",
                saved.getId(), stockItem.getId(), saved.getMaintenanceType(), currentEmployee.getId());

        return carMaintenanceMapper.toResponse(saved);
    }

    // beklenen bitiş tarihi aracın durumunu değiştirmez; araç yalnızca complete ile satışa döner
    @Transactional
    @Override
    public CarMaintenanceResponseDto updateCarMaintenance(UpdateCarMaintenanceRequestDto requestDto, Long id) {

        CarMaintenance maintenance = getCarMaintenanceEntityById(id);
        checkCarMaintenanceAccess(maintenance);
        requireNotCompleted(maintenance);

        checkNotBeforeStartDate(maintenance.getStartDate(), requestDto.getExpectedEndDate(), "Beklenen bitiş tarihi");

        carMaintenanceMapper.updateCarMaintenanceFromDto(requestDto, maintenance);

        CarMaintenance updated = carMaintenanceRepository.saveAndFlush(maintenance);

        log.info("Bakım kaydı güncellendi. id: {}, beklenen bitiş: {}, ücret: {}", id, updated.getExpectedEndDate(), updated.getCost());
        return carMaintenanceMapper.toResponse(updated);
    }

    // araç ustadan teslim alındı: bakım kapanır, araç satışa döner
    @Transactional
    @Override
    public CarMaintenanceResponseDto completeCarMaintenance(CompleteCarMaintenanceRequestDto requestDto, Long id) {

        CarMaintenance maintenance = getCarMaintenanceEntityById(id);
        checkCarMaintenanceAccess(maintenance);
        requireNotCompleted(maintenance);

        // teslim kayda geç girildiyse gerçek teslim günü gönderilir; gelmezse bugün yazılır
        LocalDate completedAt = requestDto != null && requestDto.getCompletedAt() != null
                ? requestDto.getCompletedAt()
                : LocalDate.now();

        checkNotBeforeStartDate(maintenance.getStartDate(), completedAt, "Teslim tarihi");

        maintenance.setCompletedAt(completedAt);
        maintenance.getStockItem().setStatus(CarStatus.AVAILABLE);

        CarMaintenance completed = carMaintenanceRepository.saveAndFlush(maintenance);

        log.info("Bakım tamamlandı, araç satışa döndü. id: {}, stockItemId: {}, beklenen bitiş: {}, teslim: {}",
                id, completed.getStockItem().getId(), completed.getExpectedEndDate(), completed.getCompletedAt());
        return carMaintenanceMapper.toResponse(completed);
    }

    @Transactional
    @Override
    public CarMaintenanceResponseDto findCarMaintenanceById(Long id) {

        CarMaintenance maintenance = getCarMaintenanceEntityById(id);
        checkCarMaintenanceAccess(maintenance);

        return carMaintenanceMapper.toResponse(maintenance);
    }

    @Transactional
    @Override
    public List<CarMaintenanceResponseDto> findAllCarMaintenances() {

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();

        List<CarMaintenance> maintenances;
        if (securityService.isSuperAdmin()) {
            maintenances = carMaintenanceRepository.findAllByOrderByStartDateDesc();
        } else if (currentEmployee.getRole() == Role.SALES_REP) {
            maintenances = carMaintenanceRepository.findAllByEmployeeIdOrderByStartDateDesc(currentEmployee.getId());
        } else {
            maintenances = carMaintenanceRepository.findAllByStockItem_Branch_IdOrderByStartDateDesc(securityService.getCurrentBranchId());
        }

        return maintenances.stream()
                .map(carMaintenanceMapper::toResponse)
                .toList();
    }

    // yalnızca yanlış açılmış (henüz tamamlanmamış) bakım silinir; tamamlanmış bakımın maliyeti geçmişte kalmalı
    @Transactional
    @Override
    public void deleteCarMaintenance(Long id) {

        CarMaintenance maintenance = getCarMaintenanceEntityById(id);
        checkCarMaintenanceAccess(maintenance);
        requireNotCompleted(maintenance);

        maintenance.getStockItem().setStatus(CarStatus.AVAILABLE);
        maintenance.softDelete(securityService.getCurrentEmployee());
        carMaintenanceRepository.saveAndFlush(maintenance);

        log.info("Bakım kaydı silindi. id: {}, stockItemId: {}", id, maintenance.getStockItem().getId());
    }

    private CarMaintenance getCarMaintenanceEntityById(Long id) {
        return carMaintenanceRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.MAINTENANCE_NOT_FOUND, id.toString())));
    }

    // şube kontrolüne ek olarak: satış temsilcisi yalnızca kendi açtığı bakım kaydına erişebilir
    private void checkCarMaintenanceAccess(CarMaintenance maintenance) {

        securityService.checkBranchAccess(maintenance.getStockItem().getBranch().getId());

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();
        if (currentEmployee.getRole() == Role.SALES_REP && !maintenance.getEmployee().getId().equals(currentEmployee.getId())) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED,
                    "Sadece kendi açtığınız bakım kayıtlarına erişebilirsiniz."));
        }
    }

    private void requireNotCompleted(CarMaintenance maintenance) {
        if (maintenance.getCompletedAt() != null) {
            throw new BaseException(new ErrorMessage(MessageType.MAINTENANCE_ALREADY_COMPLETED, maintenance.getId().toString()));
        }
    }

    private void checkNotBeforeStartDate(LocalDate startDate, LocalDate date, String label) {
        if (date.isBefore(startDate)) {
            throw new BaseException(new ErrorMessage(MessageType.VALIDATION_ERROR,
                    label + " başlangıç tarihinden (" + startDate + ") önce olamaz."));
        }
    }
}
