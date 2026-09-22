package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.ICustomerController;
import fdn.fdncargallery.dto.customer.CreateCustomerRequestDto;
import fdn.fdncargallery.dto.customer.CustomerResponseDto;
import fdn.fdncargallery.dto.customer.SearchCustomerRequestDto;
import fdn.fdncargallery.dto.customer.UpdateCustomerRequestDto;
import fdn.fdncargallery.service.interfaces.ICustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN', 'MANAGER')")
public class CustomerController implements ICustomerController {

    private final ICustomerService customerService;

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','BRANCH_ADMIN','MANAGER','SALES_REP')")
    @PostMapping("/create_customer")
    public ResponseEntity<CustomerResponseDto> createCustomer(@Valid @RequestBody CreateCustomerRequestDto createCustomerRequestDto) {
        CustomerResponseDto response = customerService.createCustomer(createCustomerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','BRANCH_ADMIN','MANAGER','SALES_REP')")
    @PutMapping("/update_customer/{id}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(@Valid @RequestBody UpdateCustomerRequestDto updateCustomerRequestDto,
                                                              @PathVariable Long id) {
        return ResponseEntity.ok(customerService.updateCustomer(updateCustomerRequestDto, id));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','BRANCH_ADMIN','MANAGER','SALES_REP')")
    @GetMapping("/list_customer/{id}")
    public ResponseEntity<CustomerResponseDto> findCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findCustomerById(id));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','BRANCH_ADMIN','MANAGER','SALES_REP')")
    @GetMapping("/list_customer")
    public ResponseEntity<List<CustomerResponseDto>> findAllCustomers() {
        return ResponseEntity.ok(customerService.findAllCustomers());
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','BRANCH_ADMIN','MANAGER','SALES_REP')")
    @PostMapping("/search_customer")
    public ResponseEntity<CustomerResponseDto> findCustomerByIdentityNumber(@Valid @RequestBody SearchCustomerRequestDto searchCustomerRequestDto) {
        return ResponseEntity.ok(customerService.findCustomerByIdentityNumber(searchCustomerRequestDto));
    }

    // satış temsilcisi silemez: sınıf seviyesindeki yetki geçerli
    @DeleteMapping("/delete_customer/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    // satış temsilcisi geri alamaz: silme yetkisi olan geri alır
    @PutMapping("/reactivate_customer/{id}")
    public ResponseEntity<CustomerResponseDto> reactivateCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.reactivateCustomer(id));
    }
}