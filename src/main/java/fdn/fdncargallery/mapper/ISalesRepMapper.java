package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.salesRep.CreateSalesRepRequestDto;
import fdn.fdncargallery.dto.salesRep.SalesRepResponseDto;
import fdn.fdncargallery.entity.SalesRep;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, uses = {IAddressMapper.class}, componentModel = "spring")
public interface ISalesRepMapper {

    // active ve monthlySalesCount: entity'de varsayılan değerleri var, DTO'dan gelmez
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "monthlySalesCount", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "userAccount", ignore = true)
    SalesRep toSalesRepEntity(CreateSalesRepRequestDto request);

    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.branchName")
    @Mapping(target = "username", source = "userAccount.username")
    @Mapping(target = "email", source = "userAccount.email")
    @Mapping(target = "role", source = "userAccount.role")
    @Mapping(target = "firstLogin", source = "userAccount.firstLogin")
    SalesRepResponseDto toSalesRepResponse(SalesRep salesRep);
}
