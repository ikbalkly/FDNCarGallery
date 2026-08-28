package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.carPurchase.CarPurchaseResponseDto;
import fdn.fdncargallery.dto.carPurchase.CreateCarPurchaseRequestDto;
import fdn.fdncargallery.entity.CarPurchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ICarPurchaseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "stockItem", ignore = true)
    @Mapping(target = "sellerCustomer", ignore = true)
    @Mapping(target = "employee", ignore = true)
    CarPurchase toEntity(CreateCarPurchaseRequestDto request);


    // Stok kalemi ve araç bilgileri
    @Mapping(target = "stockItemId", source = "stockItem.id")
    @Mapping(target = "plateNumber", source = "stockItem.plateNumber")
    @Mapping(target = "vin", source = "stockItem.vehicle.vin")
    @Mapping(target = "brandAndModel", expression = "java(purchase.getStockItem().getVehicle().getBrand() + \" \" + purchase.getStockItem().getVehicle().getModel())")

    // Satıcı müşteri bilgileri
    @Mapping(target = "sellerCustomerId", source = "sellerCustomer.id")
    @Mapping(target = "sellerIdentityNumber", source = "sellerCustomer.identityNumber")
    @Mapping(target = "sellerCustomerFullName", expression = "java(purchase.getSellerCustomer().getFirstName() + \" \" + purchase.getSellerCustomer().getLastName())")

    // İşlemi yapan personel
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeFullName", expression = "java(purchase.getEmployee().getName() + \" \" + purchase.getEmployee().getSurname())")
    CarPurchaseResponseDto toResponse(CarPurchase purchase);
}
