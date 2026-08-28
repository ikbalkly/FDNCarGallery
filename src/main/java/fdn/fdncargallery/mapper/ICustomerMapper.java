package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.customer.CreateCustomerRequestDto;
import fdn.fdncargallery.dto.customer.CustomerResponseDto;
import fdn.fdncargallery.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {IAddressMapper.class}, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ICustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Customer toEntity(CreateCustomerRequestDto createCustomerRequestDto);

    CustomerResponseDto toDto(Customer customer);
}
