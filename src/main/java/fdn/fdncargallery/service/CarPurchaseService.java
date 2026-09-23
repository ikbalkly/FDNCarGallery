package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.carPurchase.CarPurchaseResponseDto;
import fdn.fdncargallery.dto.carPurchase.CreateCarPurchaseRequestDto;
import fdn.fdncargallery.dto.carPurchase.UpdateCarPurchaseRequestDto;
import fdn.fdncargallery.dto.stockItem.StockItemResponseDto;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.CarPurchase;
import fdn.fdncargallery.entity.Customer;
import fdn.fdncargallery.entity.StockItem;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.ICarPurchaseMapper;
import fdn.fdncargallery.repository.ICarPurchaseRepository;
import fdn.fdncargallery.service.interfaces.ICarPurchaseService;
import fdn.fdncargallery.service.interfaces.ICustomerService;
import fdn.fdncargallery.service.interfaces.IStockItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarPurchaseService implements ICarPurchaseService {

    private final ICarPurchaseRepository carPurchaseRepository;
    private final ICarPurchaseMapper carPurchaseMapper;
    private final IStockItemService stockItemService;
    private final ICustomerService customerService;
    private final SecurityService securityService;

    @Transactional
    @Override
    public CarPurchaseResponseDto createCarPurchase(CreateCarPurchaseRequestDto requestDto) {

        // satıcı müşteri önceden search_customer/create_customer ile bulunup/açılıp id'si buraya gelir
        Customer sellerCustomer = customerService.getCustomerEntityById(requestDto.getSellerCustomerId());

        // stok girişi kuralları burada tekrar yazılmıyor, StockItemService üzerinden yapılıyor
        StockItemResponseDto createdStockItem = stockItemService.createStockItem(requestDto.getStockItem());
        StockItem stockItem = stockItemService.getStockItemEntityById(createdStockItem.getId());

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();

        CarPurchase carPurchase = carPurchaseMapper.toEntity(requestDto);
        carPurchase.setStockItem(stockItem);
        carPurchase.setSellerCustomer(sellerCustomer);
        carPurchase.setEmployee(currentEmployee);

        CarPurchase saved = carPurchaseRepository.saveAndFlush(carPurchase);

        log.info("Araç alışı kaydedildi. id: {}, stockItemId: {}, satıcı id: {}, personel id: {}",
                saved.getId(), stockItem.getId(), sellerCustomer.getId(), currentEmployee.getId());

        return carPurchaseMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public CarPurchaseResponseDto updateCarPurchase(UpdateCarPurchaseRequestDto requestDto, Long id) {

        CarPurchase carPurchase = getCarPurchaseEntityById(id);
        checkCarPurchaseAccess(carPurchase);

        carPurchaseMapper.updateCarPurchaseFromDto(requestDto, carPurchase);

        CarPurchase updated = carPurchaseRepository.saveAndFlush(carPurchase);

        log.info("Araç alış kaydı güncellendi. id: {}", id);
        return carPurchaseMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public CarPurchaseResponseDto findCarPurchaseById(Long id) {

        CarPurchase carPurchase = getCarPurchaseEntityById(id);
        checkCarPurchaseAccess(carPurchase);

        return carPurchaseMapper.toResponse(carPurchase);
    }

    @Transactional
    @Override
    public List<CarPurchaseResponseDto> findAllCarPurchases() {

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();

        List<CarPurchase> purchases;
        if (securityService.isSuperAdmin()) {
            // tümü
            purchases = carPurchaseRepository.findAllByOrderByPurchaseDateDesc();
        } else if (currentEmployee.getRole() == Role.SALES_REP) {
            // satış temsilcisi yalnızca kendi yaptığı alışları görür
            purchases = carPurchaseRepository.findAllByEmployeeIdOrderByPurchaseDateDesc(currentEmployee.getId());
        } else {
            // branch admin ve müdür kendi şubesindeki tüm alışları görür
            purchases = carPurchaseRepository.findAllByStockItem_Branch_IdOrderByPurchaseDateDesc(securityService.getCurrentBranchId());
        }

        return purchases.stream()
                .map(carPurchaseMapper::toResponse)
                .toList();
    }

    private CarPurchase getCarPurchaseEntityById(Long id) {
        return carPurchaseRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PURCHASE_RECORD_NOT_FOUND, id.toString())));
    }

    // şube kontrolüne ek olarak: satış temsilcisi yalnızca kendi oluşturduğu alış kaydına erişebilir
    private void checkCarPurchaseAccess(CarPurchase carPurchase) {

        securityService.checkBranchAccess(carPurchase.getStockItem().getBranch().getId());

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();
        if (currentEmployee.getRole() == Role.SALES_REP && !carPurchase.getEmployee().getId().equals(currentEmployee.getId())) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED,
                    "Sadece kendi oluşturduğunuz alış kayıtlarına erişebilirsiniz."));
        }
    }
}
