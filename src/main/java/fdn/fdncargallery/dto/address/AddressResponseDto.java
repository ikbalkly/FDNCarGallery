package fdn.fdncargallery.dto.address;

import lombok.*;

// Adres gömülü bir değer nesnesi: kendi id'si ve zaman damgaları yok,
// bu yüzden BaseEntityResponseDto'yu genişletmiyor.
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
    private String zipCode;
    private String fullAddress;
}
