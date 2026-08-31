package fdn.fdncargallery.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDto {

    @NotBlank(message = "İl (Şehir) alanı boş bırakılamaz!")
    @Size(min = 3, max = 50, message = "İl adı en az 3, en fazla 50 karakter olmalıdır.")
    private String city;

    @NotBlank(message = "İlçe alanı boş bırakılamaz!")
    @Size(min = 2, max = 50, message = "İlçe adı en az 2, en fazla 50 karakter olmalıdır.")
    private String district;

    @NotBlank(message = "Mahalle alanı boş bırakılamaz!")
    private String neighborhood;

    @NotBlank(message = "Sokak alanı boş bırakılamaz!")
    private String street;

    private String zipCode;

    @NotBlank(message = "Açık adres detayları boş bırakılamaz!")
    @Size(min = 10, max = 255, message = "Açık adres en az 10, en fazla 255 karakter olmalıdır.")
    private String fullAddress;
}
