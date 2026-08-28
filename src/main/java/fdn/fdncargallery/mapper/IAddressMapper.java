package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.address.AddressRequestDto;
import fdn.fdncargallery.dto.address.AddressResponseDto;
import fdn.fdncargallery.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface IAddressMapper {

    // bu alanlar dtodan gelmez
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Address toEntity(AddressRequestDto addressRequestDto);

    AddressResponseDto toDto(Address address);


    // zaten var olan datayı günceller
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateAddressFromDto(AddressRequestDto addressRequestDto, @MappingTarget Address address);
}
