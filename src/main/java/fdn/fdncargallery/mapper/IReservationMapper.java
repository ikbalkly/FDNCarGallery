package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.reservation.CreateReservationRequestDto;
import fdn.fdncargallery.dto.reservation.ReservationResponseDto;
import fdn.fdncargallery.dto.reservation.UpdateReservationRequestDto;
import fdn.fdncargallery.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = IBaseMapperConfig.class)
public interface IReservationMapper {

    @Mapping(target = "stockItem", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "reservationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    Reservation toEntity(CreateReservationRequestDto request);


    @Mapping(target = "stockItemId", source = "stockItem.id")
    @Mapping(target = "plateNumber", source = "stockItem.plateNumber")
    @Mapping(target = "vin", source = "stockItem.vehicle.vin")
    @Mapping(target = "brandAndModel", expression = "java(reservation.getStockItem().getVehicle().getBrand().getBrandName() + \" \" + reservation.getStockItem().getVehicle().getModel().getModelName())")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerIdentityNumber", source = "customer.identityNumber")
    @Mapping(target = "customerFullName", expression = "java(reservation.getCustomer().getFirstName() + \" \" + reservation.getCustomer().getLastName())")
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeFullName", expression = "java(reservation.getEmployee().getName() + \" \" + reservation.getEmployee().getSurname())")
    @Mapping(target = "active", expression = "java(reservation.isActive())")
    @Mapping(target = "expired", expression = "java(reservation.isExpired())")
    ReservationResponseDto toResponse(Reservation reservation);


    @Mapping(target = "stockItem", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "reservationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateReservationFromDto(UpdateReservationRequestDto request, @MappingTarget Reservation reservation);
}
