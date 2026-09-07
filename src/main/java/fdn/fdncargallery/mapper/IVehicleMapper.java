package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.vehicle.CreateVehicleRequestDto;
import fdn.fdncargallery.dto.vehicle.UpdateVehicleRequestDto;
import fdn.fdncargallery.dto.vehicle.VehicleResponseDto;
import fdn.fdncargallery.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = IBaseMapperConfig.class)
public interface IVehicleMapper {

    // brand/model referans veri: adıyla geliyor, servis katmanında çözülüp set ediliyor.
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "stockItems", ignore = true)
    Vehicle toEntity(CreateVehicleRequestDto request);

    @Mapping(target = "brand", source = "brand.brandName")
    @Mapping(target = "model", source = "model.modelName")
    VehicleResponseDto toResponse(Vehicle vehicle);

    // vin ignore: entity'de updatable = false, kimlik değişmez.
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "vin", ignore = true)
    @Mapping(target = "stockItems", ignore = true)
    void updateVehicleFromDto(UpdateVehicleRequestDto request, @MappingTarget Vehicle vehicle);
}
