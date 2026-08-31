package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.address.AddressRequestDto;
import fdn.fdncargallery.dto.address.AddressResponseDto;
import fdn.fdncargallery.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface IAddressMapper {

    // Address artık gömülü bir değer nesnesi: id/createTime/updateTime yok,
    // bu yüzden ignore edilecek alan da kalmadı.
    Address toEntity(AddressRequestDto addressRequestDto);

    AddressResponseDto toDto(Address address);

    // zaten var olan datayı günceller
    void updateAddressFromDto(AddressRequestDto addressRequestDto, @MappingTarget Address address);
}
