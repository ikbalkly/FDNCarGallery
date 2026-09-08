package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.BaseEntityResponseDto;
import fdn.fdncargallery.entity.BaseEntity;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.WARN,
        mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG
)
public interface IBaseMapperConfig {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    BaseEntity anyDtoToBaseEntity(Object dto);

    @Mapping(target = "deletedById", source = "deletedBy.id")
    @Mapping(target = "deletedByFullName", source = "deletedBy.fullName")
    BaseEntityResponseDto baseEntityToResponse(BaseEntity entity);
}
