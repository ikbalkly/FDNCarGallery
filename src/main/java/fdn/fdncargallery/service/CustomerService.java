package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.customer.CreateCustomerRequestDto;
import fdn.fdncargallery.dto.customer.CustomerResponseDto;
import fdn.fdncargallery.dto.customer.SearchCustomerRequestDto;
import fdn.fdncargallery.dto.customer.UpdateCustomerRequestDto;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.Customer;
import fdn.fdncargallery.enums.CustomerType;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.ICustomerMapper;
import fdn.fdncargallery.repository.ICustomerRepository;
import fdn.fdncargallery.service.interfaces.ICustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {

    private final ICustomerRepository customerRepository;
    private final ICustomerMapper customerMapper;
    private final SecurityService securityService;

    @Transactional
    @Override
    public CustomerResponseDto createCustomer(CreateCustomerRequestDto request) {

        checkIdentityNumberMatchesType(request.getCustomerType(), request.getIdentityNumber());
        checkIdentityNumberNotTaken(request.getIdentityNumber());

        Customer savedCustomer = customerRepository.saveAndFlush(customerMapper.toEntity(request));

        log.info("Müşteri kaydı açıldı. id: {}, personel id: {}", savedCustomer.getId(), securityService.getCurrentEmployee().getId());
        return customerMapper.toResponse(savedCustomer);
    }


    @Transactional
    @Override
    public CustomerResponseDto updateCustomer(UpdateCustomerRequestDto request, Long id) {

        Customer existingCustomer = getCustomerEntityById(id);

        checkIdentityNumberMatchesType(request.getCustomerType(), request.getIdentityNumber());

        // TC değiştiyse yeni TC başka bir kayıtta olmamalı
        if (!existingCustomer.getIdentityNumber().equals(request.getIdentityNumber())) {
            checkIdentityNumberNotTaken(request.getIdentityNumber());
        }

        customerMapper.updateCustomerFromDto(request, existingCustomer);

        Customer updatedCustomer = customerRepository.saveAndFlush(existingCustomer);

        log.info("Müşteri güncellendi. id: {}", updatedCustomer.getId());
        return customerMapper.toResponse(updatedCustomer);
    }

    @Transactional
    @Override
    public CustomerResponseDto findCustomerById(Long id) {
        return customerMapper.toResponse(getCustomerEntityById(id));
    }

    @Transactional
    @Override
    public List<CustomerResponseDto> findAllCustomers() {
        return customerRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    // silinmişler de döner: geri alınacak kaydın id'si buradan bulunur, cevapta deletedAt dolu gelir
    @Transactional
    @Override
    public CustomerResponseDto findCustomerByIdentityNumber(SearchCustomerRequestDto request) {

        Customer customer = customerRepository.findByIdentityNumber(request.getIdentityNumber())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.CUSTOMER_NOT_FOUND, "Bu kimlik numarasıyla kayıtlı müşteri yok")));

        log.info("Kimlik numarası ile müşteri araması yapıldı. bulunan müşteri id: {}", customer.getId());
        return customerMapper.toResponse(customer);
    }

    @Transactional
    @Override
    public void deleteCustomer(Long id) {

        Customer customer = getCustomerEntityById(id);
        BaseEmployee actor = securityService.getCurrentEmployee();

        // kalıcı silme yok: satış ve rezervasyon kayıtları müşteriye bağlı kalır, kimin sildiği denetim izine yazılır
        customer.softDelete(actor);
        customerRepository.saveAndFlush(customer);

        log.info("Müşteri silindi. id: {}, personel id: {}", id, actor.getId());
    }

    // Bilgiler değişmişse geri almadan sonra update_customer çağrılır; bu uç yalnızca kaydı geri getirir.
    @Transactional
    @Override
    public CustomerResponseDto reactivateCustomer(Long id) {

        // getCustomerEntityById silinmiş kaydı 404 sayar; burada tam da silinmiş kayıt aranıyor
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.CUSTOMER_NOT_FOUND, id.toString())));

        if (!customer.isDeleted()) {
            throw new BaseException(new ErrorMessage(MessageType.CUSTOMER_ALREADY_ACTIVE, id.toString()));
        }

        // restore() deletedBy'ı da temizliyor: kimin sildiği bundan sonra yalnızca silme logunda kalır
        customer.restore();
        Customer reactivatedCustomer = customerRepository.saveAndFlush(customer);

        log.info("Müşteri kaydı geri alındı. id: {}, personel id: {}", id, securityService.getCurrentEmployee().getId());
        return customerMapper.toResponse(reactivatedCustomer);
    }

    @Override
    public Customer getCustomerEntityById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.CUSTOMER_NOT_FOUND, id.toString())));

        if (customer.isDeleted()) {
            throw new BaseException(new ErrorMessage(MessageType.CUSTOMER_NOT_FOUND, id.toString()));
        }
        return customer;
    }

    // bireysel müşteri 11 haneli TCKN, kurumsal müşteri 10 haneli VKN ile kaydolur; DTO ikisini de kabul ediyor
    private void checkIdentityNumberMatchesType(CustomerType customerType, String identityNumber) {
        int expectedLength = customerType == CustomerType.INDIVIDUAL ? 11 : 10;
        if (identityNumber.length() != expectedLength) {
            throw new BaseException(new ErrorMessage(MessageType.INVALID_IDENTITY_NUMBER, identityNumber));
        }
    }

    // TC sütunu tekil ve silinen müşterinin satırı tabloda kalıyor: silinmiş kayıt da TC'yi tutar
    private void checkIdentityNumberNotTaken(String identityNumber) {
        customerRepository.findByIdentityNumber(identityNumber).ifPresent(existing -> {
            if (!existing.isDeleted()) {
                throw new BaseException(new ErrorMessage(MessageType.CUSTOMER_ALREADY_EXISTS, identityNumber));
            }
            // Silinmiş kayıt: yeni satır değil, geri alma gerekiyor. id'yi mesaja koy.
            throw new BaseException(new ErrorMessage(MessageType.CUSTOMER_DELETED_RECORD_EXISTS, existing.getId().toString()));
        });
    }
}
