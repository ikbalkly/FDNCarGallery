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
    // hesap alanları sunucuda üretiliyor; çıkış tarihi kayıt anında boş
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "firstLogin", ignore = true)
    @Mapping(target = "terminationDate", ignore = true)
    SalesRep toSalesRepEntity(CreateSalesRepRequestDto request);

    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.branchName")
    SalesRepResponseDto toSalesRepResponse(SalesRep salesRep);
}
