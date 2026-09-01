package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.manager.CreateManagerRequestDto;
import fdn.fdncargallery.dto.manager.ManagerResponseDto;
import fdn.fdncargallery.dto.manager.UpdateManagerRequestDto;
import fdn.fdncargallery.entity.Manager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, uses = {IAddressMapper.class}, componentModel = "spring")
public interface IManagerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "branch", ignore = true)
    // hesap alanları sunucuda üretiliyor; çıkış tarihi kayıt anında boş
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "firstLogin", ignore = true)
    @Mapping(target = "terminationDate", ignore = true)
    Manager toEntity(CreateManagerRequestDto request);

    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.branchName")
    ManagerResponseDto toResponse(Manager manager);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "identityNumber", ignore = true)
    // kullanıcı adı, şifre ve rol update ile değişmez; işe giriş/çıkış tarihi de bu DTO'da yok
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "firstLogin", ignore = true)
    @Mapping(target = "hireDate", ignore = true)
    @Mapping(target = "terminationDate", ignore = true)
    void updateManagerFromDto(UpdateManagerRequestDto request, @MappingTarget Manager manager);
}
