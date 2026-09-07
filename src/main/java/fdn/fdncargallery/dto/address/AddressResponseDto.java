package fdn.fdncargallery.dto.address;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AddressResponseDto {
    private String city;
    private String district;
    private String neighborhood;
    private String street;
    private String buildingName;
    private String buildingNo;
    private String doorNo;
    private String zipCode;
    private String fullAddress;
}
