package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.soldCar.CreateSoldCarRequestDto;
import fdn.fdncargallery.dto.soldCar.SoldCarResponseDto;
import fdn.fdncargallery.entity.SoldCar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = IBaseMapperConfig.class)
public interface ISoldCarMapper {

    // İlişkisel nesneler, satış tarihi ve prim oranı servis katmanında set edilir.
    // commissionRate özellikle istemciden ALINMAZ: satış anındaki oran satıcıdan kopyalanır (temsilci değilse 0).
    @Mapping(target = "stockItem", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "saleDate", ignore = true)
    @Mapping(target = "commissionRate", ignore = true)
    SoldCar toEntity(CreateSoldCarRequestDto request);

    // Stok kalemi ve araç bilgileri
    @Mapping(target = "stockItemId", source = "stockItem.id")
    @Mapping(target = "plateNumber", source = "stockItem.plateNumber")
    @Mapping(target = "vin", source = "stockItem.vehicle.vin")
    @Mapping(target = "brandAndModel", expression = "java(soldCar.getStockItem().getVehicle().getBrand().getBrandName() + \" \" + soldCar.getStockItem().getVehicle().getModel().getModelName())")

    // Müşteri bilgileri
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerIdentityNumber", source = "customer.identityNumber")
    @Mapping(target = "customerFullName", expression = "java(soldCar.getCustomer().getFirstName() + \" \" + soldCar.getCustomer().getLastName())")

    // Satışı yapan personel
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeFullName", source = "employee.fullName")
    SoldCarResponseDto toResponse(SoldCar soldCar);
}
