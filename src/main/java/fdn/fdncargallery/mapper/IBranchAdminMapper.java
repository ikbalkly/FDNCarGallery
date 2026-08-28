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
    @Mapping(target = "userAccount", ignore = true)
    SystemAdmin toEntity(CreateBranchAdminRequestDto request);

    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.branchName")
    @Mapping(target = "username", source = "userAccount.username")
    @Mapping(target = "email", source = "userAccount.email")
    @Mapping(target = "role", source = "userAccount.role")
    @Mapping(target = "firstLogin", source = "userAccount.firstLogin")
    @Mapping(target = "temporaryPassword", ignore = true)
    BranchAdminResponseDto toResponse(SystemAdmin branchAdmin);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "userAccount", ignore = true)
    // identityNumber ignore: TC değişmez (entity'de updatable=false), update DTO'sunda bilerek yok.
    @Mapping(target = "identityNumber", ignore = true)
    void updateBranchAdminFromDto(UpdateBranchAdminRequestDto request, @MappingTarget SystemAdmin branchAdmin);
}
