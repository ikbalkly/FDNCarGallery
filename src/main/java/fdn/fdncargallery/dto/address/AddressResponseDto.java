package fdn.fdncargallery.dto.address;

import fdn.fdncargallery.dto.BaseEntityResponseDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AddressResponseDto extends BaseEntityResponseDto {
    private String city;
    private String district;
    private String neighborhood;
    private String street;
    private String zipCode;
    private String fullAddress;
}
