package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.salesRep.CreateSalesRepRequestDto;
import fdn.fdncargallery.dto.salesRep.SalesRepResponseDto;
import fdn.fdncargallery.entity.SalesRep;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = IBaseMapperConfig.class, uses = {IAddressMapper.class})
public interface ISalesRepMapper {

    // authorities: UserDetails'ten gelen türetilmiş koleksiyon, role'den üretilir.
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "monthlySalesCount", ignore = true)
    @Mapping(target = "branch", ignore = true)
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
