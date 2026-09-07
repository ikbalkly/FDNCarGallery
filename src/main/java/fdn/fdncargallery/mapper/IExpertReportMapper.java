package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.expertReport.CreateExpertReportRequestDto;
import fdn.fdncargallery.dto.expertReport.ExpertReportResponseDto;
import fdn.fdncargallery.entity.ExpertReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = IBaseMapperConfig.class)
public interface IExpertReportMapper {

    // stockItem servis katmanında id ile bulunarak set edilir.
    @Mapping(target = "stockItem", ignore = true)
    ExpertReport toEntity(CreateExpertReportRequestDto request);

    // --- 2. Entity'den Response'a ---
    @Mapping(target = "stockItemId", source = "stockItem.id")
    @Mapping(target = "plateNumber", source = "stockItem.plateNumber")
    @Mapping(target = "vin", source = "stockItem.vehicle.vin")
    @Mapping(target = "brandAndModel", expression = "java(expertReport.getStockItem().getVehicle().getBrand().getBrandName() + \" \" + expertReport.getStockItem().getVehicle().getModel().getModelName())")
    ExpertReportResponseDto toResponse(ExpertReport expertReport);
}
