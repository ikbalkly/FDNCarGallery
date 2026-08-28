package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.address.AddressRequestDto;
import fdn.fdncargallery.dto.address.AddressResponseDto;
import fdn.fdncargallery.entity.Address;

import java.util.List;

public interface IAddressService {

    public AddressResponseDto createAddress(AddressRequestDto addressRequestDto);

    public AddressResponseDto updateAddress(AddressRequestDto addressRequestDto, Long id);

    public AddressResponseDto getAddressById(Long id);

    public void deleteAddress(Long id);

    public List<AddressResponseDto> getAllAddresses();

    public Address getAddressEntityById(Long id);

}
