package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.model.CreateModelRequestDto;
import fdn.fdncargallery.dto.model.ModelResponseDto;
import fdn.fdncargallery.entity.Model;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = IBaseMapperConfig.class)
public interface IModelMapper {

    @Mapping(target = "brand", ignore = true)
    Model toEntity(CreateModelRequestDto request);

    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "brandName", source = "brand.brandName")
    ModelResponseDto toResponse(Model model);
}
