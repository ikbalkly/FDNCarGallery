package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.branchAdmin.BranchAdminResponseDto;
import fdn.fdncargallery.dto.branchAdmin.CreateBranchAdminRequestDto;
import fdn.fdncargallery.dto.branchAdmin.UpdateBranchAdminRequestDto;
import fdn.fdncargallery.entity.SystemAdmin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, uses = {IAddressMapper.class}, componentModel = "spring")
public interface IBranchAdminMapper {

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
    SystemAdmin toEntity(CreateBranchAdminRequestDto request);

    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.branchName")
    BranchAdminResponseDto toResponse(SystemAdmin branchAdmin);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "branch", ignore = true)
    // identityNumber ignore: TC değişmez (entity'de updatable=false), update DTO'sunda bilerek yok.
    @Mapping(target = "identityNumber", ignore = true)
    // kullanıcı adı, şifre ve rol update ile değişmez; işe giriş/çıkış tarihi de bu DTO'da yok
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "firstLogin", ignore = true)
    @Mapping(target = "hireDate", ignore = true)
    @Mapping(target = "terminationDate", ignore = true)
    void updateBranchAdminFromDto(UpdateBranchAdminRequestDto request, @MappingTarget SystemAdmin branchAdmin);
}
