package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.carMaintenance.CarMaintenanceResponseDto;
import fdn.fdncargallery.dto.carMaintenance.CreateCarMaintenanceRequestDto;
import fdn.fdncargallery.entity.CarMaintenance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ICarMaintenanceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "stockItem", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    CarMaintenance toEntity(CreateCarMaintenanceRequestDto request);


    // Stok kalemi ve araç bilgileri
    @Mapping(target = "stockItemId", source = "stockItem.id")
    @Mapping(target = "plateNumber", source = "stockItem.plateNumber")
    @Mapping(target = "brandAndModel", expression = "java(maintenance.getStockItem().getVehicle().getBrand() + \" \" + maintenance.getStockItem().getVehicle().getModel())")

    // Bakım hâlâ açık mı? Tek doğruluk kaynağı endDate.
    @Mapping(target = "completed", expression = "java(maintenance.getEndDate() != null)")

    // Bakımı açan personel
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeFullName", expression = "java(maintenance.getEmployee().getName() + \" \" + maintenance.getEmployee().getSurname())")
    CarMaintenanceResponseDto toResponse(CarMaintenance maintenance);
}
