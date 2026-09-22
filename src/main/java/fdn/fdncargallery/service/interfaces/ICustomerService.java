package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.customer.CreateCustomerRequestDto;
import fdn.fdncargallery.dto.customer.CustomerResponseDto;
import fdn.fdncargallery.dto.customer.SearchCustomerRequestDto;
import fdn.fdncargallery.dto.customer.UpdateCustomerRequestDto;
import fdn.fdncargallery.entity.Customer;

import java.util.List;

public interface ICustomerService {

    CustomerResponseDto createCustomer(CreateCustomerRequestDto request);

    CustomerResponseDto updateCustomer(UpdateCustomerRequestDto request, Long id);

    CustomerResponseDto findCustomerById(Long id);

    List<CustomerResponseDto> findAllCustomers();

    // kimlik numarası ile arar; silinmiş kayıtları da döndürür
    CustomerResponseDto findCustomerByIdentityNumber(SearchCustomerRequestDto request);

    void deleteCustomer(Long id);

    // silinmiş müşteri kaydını geri alır
    CustomerResponseDto reactivateCustomer(Long id);

    // satış ve rezervasyon servisleri müşteriyi buradan alacak
    Customer getCustomerEntityById(Long id);
}
