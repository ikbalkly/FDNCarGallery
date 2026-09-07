package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.brand.BrandResponseDto;
import fdn.fdncargallery.dto.brand.CreateBrandRequestDto;
import fdn.fdncargallery.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = IBaseMapperConfig.class)
public interface IBrandMapper {

    @Mapping(target = "models", ignore = true)
    Brand toEntity(CreateBrandRequestDto request);

    BrandResponseDto toResponse(Brand brand);
}
