package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.customer.CreateCustomerRequestDto;
import fdn.fdncargallery.dto.customer.CustomerResponseDto;
import fdn.fdncargallery.dto.customer.SearchCustomerRequestDto;
import fdn.fdncargallery.dto.customer.UpdateCustomerRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICustomerController {

    public ResponseEntity<CustomerResponseDto> createCustomer(CreateCustomerRequestDto createCustomerRequestDto);

    public ResponseEntity<CustomerResponseDto> updateCustomer(UpdateCustomerRequestDto updateCustomerRequestDto, Long id);

    public ResponseEntity<CustomerResponseDto> findCustomerById(Long id);

    public ResponseEntity<List<CustomerResponseDto>> findAllCustomers();

    public ResponseEntity<CustomerResponseDto> findCustomerByIdentityNumber(SearchCustomerRequestDto searchCustomerRequestDto);

    public ResponseEntity<Void> deleteCustomer(Long id);

    public ResponseEntity<CustomerResponseDto> reactivateCustomer(Long id);
}