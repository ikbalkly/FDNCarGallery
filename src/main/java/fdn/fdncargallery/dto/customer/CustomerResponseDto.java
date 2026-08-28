package fdn.fdncargallery.dto.customer;

import fdn.fdncargallery.dto.BaseEntityResponseDto;
import fdn.fdncargallery.dto.address.AddressResponseDto;
import fdn.fdncargallery.enums.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerResponseDto extends BaseEntityResponseDto {

    private CustomerType customerType;
    private String identityNumber;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    //adres bilgisi dtodan gelir
    private AddressResponseDto address;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
