package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.customer.CreateCustomerRequestDto;
import fdn.fdncargallery.dto.customer.CustomerResponseDto;
import fdn.fdncargallery.dto.customer.UpdateCustomerRequestDto;
import fdn.fdncargallery.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = IBaseMapperConfig.class, uses = {IAddressMapper.class})
public interface ICustomerMapper {

    Customer toEntity(CreateCustomerRequestDto createCustomerRequestDto);

    CustomerResponseDto toResponse(Customer customer);

    // adres dahil tüm alanlar yerinde güncellenir
    void updateCustomerFromDto(UpdateCustomerRequestDto request, @MappingTarget Customer customer);
}
