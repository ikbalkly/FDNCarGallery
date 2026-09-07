package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.customer.CreateCustomerRequestDto;
import fdn.fdncargallery.dto.customer.CustomerResponseDto;
import fdn.fdncargallery.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(config = IBaseMapperConfig.class, uses = {IAddressMapper.class})
public interface ICustomerMapper {

    Customer toEntity(CreateCustomerRequestDto createCustomerRequestDto);

    CustomerResponseDto toDto(Customer customer);
}
