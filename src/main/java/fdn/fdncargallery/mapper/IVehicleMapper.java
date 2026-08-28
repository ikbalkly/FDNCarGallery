package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.vehicle.CreateVehicleRequestDto;
import fdn.fdncargallery.dto.vehicle.UpdateVehicleRequestDto;
import fdn.fdncargallery.dto.vehicle.VehicleResponseDto;
import fdn.fdncargallery.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface IVehicleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "stockItems", ignore = true)
    Vehicle toEntity(CreateVehicleRequestDto request);

    VehicleResponseDto toResponse(Vehicle vehicle);

    // vin ignore: entity'de updatable = false, kimlik değişmez.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "vin", ignore = true)
    @Mapping(target = "stockItems", ignore = true)
    void updateVehicleFromDto(UpdateVehicleRequestDto request, @MappingTarget Vehicle vehicle);
}
