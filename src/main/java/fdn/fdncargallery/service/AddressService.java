package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.address.AddressRequestDto;
import fdn.fdncargallery.dto.address.AddressResponseDto;
import fdn.fdncargallery.entity.Address;
import fdn.fdncargallery.entity.Branch;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IAddressMapper;
import fdn.fdncargallery.repository.IAddressRepository;
import fdn.fdncargallery.repository.IBranchRepository;
import fdn.fdncargallery.service.interfaces.IAddressService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService {

    private final IAddressRepository addressRepository;
    private final IAddressMapper addressMapper;
    private final IBranchRepository branchRepository;
    private final SecurityService securityService;

    @Transactional
    @Override
    public AddressResponseDto createAddress(AddressRequestDto addressRequestDto) {
        /*
         * gelen dto nesnesini mapper ile entitiye çevir
         * entityi repositorye kaydet
         * return olarak tekrardan kaydedilen entityi dto çevirerek gönder
         * */

        requireSystemAdmin("Adres kaydını yalnızca sistem yöneticisi tek başına oluşturabilir.");

        Address address = addressMapper.toEntity(addressRequestDto);
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toDto(savedAddress);
    }

    @Transactional
    @Override
    public AddressResponseDto updateAddress(AddressRequestDto addressRequestDto, Long id) {

        /*
         * gönderilen id ile adresi bul veya hata fırlat
         * adresin bağlı olduğu şube üzerinden yetki kontrolü yapılır
         * dto içerisinden gelen bilgileri var olan entity içerisine setle
         * güncellenmiş olan entityi kaydet
         * dto formatına çevirerek return et
         * */

        Address existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString())));

        checkAddressAccess(id);

        addressMapper.updateAddressFromDto(addressRequestDto, existingAddress);

        Address updatedAddress = addressRepository.save(existingAddress);
        return addressMapper.toDto(updatedAddress);
    }

    @Transactional
    @Override
    public AddressResponseDto getAddressById(Long id) {
        /*
         * gönderilen id ile adresi bul ve entity olarak setle
         * eğer adres yoksa hata fırlat
         * yetki kontrolü yapılır
         * entityi tekrar dto yaparak return et
         * */
        Address address = getAddressEntityById(id);
        checkAddressAccess(id);
        return addressMapper.toDto(address);
    }

    @Transactional
    @Override
    public List<AddressResponseDto> getAllAddresses() {

        requireSystemAdmin("Tüm adresleri yalnızca sistem yöneticisi listeleyebilir.");

        List<Address> addresses = addressRepository.findAll();
        List<AddressResponseDto> responseDtoArrayList = new ArrayList<>();
        for (Address address : addresses) {
            responseDtoArrayList.add(addressMapper.toDto(address));
        }
        return responseDtoArrayList;
    }

    @Transactional
    @Override
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString())));

        checkAddressAccess(id);

        addressRepository.delete(address);
        log.info("Delete address with id {}", id);
    }

    /**
     * Servisler arası kullanım için ham entity getirir; yetki kontrolü YAPMAZ.
     * ManagerService.getManagerEntityById ve BranchService.getBranchEntityById ile
     * aynı desen: kontrolü, entity'yi hangi bağlamda kullanacağını bilen çağıran yapar.
     */
    @Override
    public Address getAddressEntityById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, id.toString())));
    }

    /**
     * Adres yetki kapısı. Adres bir şubeye aitse o şubenin erişim kuralı uygulanır;
     * muafiyet SADECE SUPER_ADMIN'e, BRANCH_ADMIN ve MANAGER kendi şubesine kilitli.
     * Adres bir şubeye ait değilse (personel ya da müşteri adresi) branchId null
     * kalır ve checkBranchAccess null'ı yalnızca SUPER_ADMIN'e geçirir.
     */
    private void checkAddressAccess(Long addressId) {
        Long addressBranchId = branchRepository.findByAddressId(addressId)
                .map(Branch::getId)
                .orElse(null);
        securityService.checkBranchAccess(addressBranchId);
    }

    /** Şube bağlamı olmayan işlemler için: yalnızca SUPER_ADMIN geçebilir. */
    private void requireSystemAdmin(String message) {
        if (!securityService.isSuperAdmin()) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, message));
        }
    }
}
