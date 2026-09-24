package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.carMaintenance.CarMaintenanceResponseDto;
import fdn.fdncargallery.dto.carMaintenance.CreateCarMaintenanceRequestDto;
import fdn.fdncargallery.dto.carMaintenance.UpdateCarMaintenanceRequestDto;
import fdn.fdncargallery.entity.CarMaintenance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = IBaseMapperConfig.class)
public interface ICarMaintenanceMapper {

    @Mapping(target = "stockItem", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    CarMaintenance toEntity(CreateCarMaintenanceRequestDto request);

    @Mapping(target = "stockItem", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    void updateCarMaintenanceFromDto(UpdateCarMaintenanceRequestDto request, @MappingTarget CarMaintenance maintenance);


    // Stok kalemi ve araç bilgileri
    @Mapping(target = "stockItemId", source = "stockItem.id")
    @Mapping(target = "plateNumber", source = "stockItem.plateNumber")
    @Mapping(target = "brandAndModel", expression = "java(maintenance.getStockItem().getVehicle().getBrand().getBrandName() + \" \" + maintenance.getStockItem().getVehicle().getModel().getModelName())")

    // Bakım hâlâ açık mı? Tek doğruluk kaynağı completedAt.
    @Mapping(target = "completed", expression = "java(maintenance.getCompletedAt() != null)")

    // Bakımı açan personel
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeFullName", expression = "java(maintenance.getEmployee().getName() + \" \" + maintenance.getEmployee().getSurname())")
    CarMaintenanceResponseDto toResponse(CarMaintenance maintenance);
}
