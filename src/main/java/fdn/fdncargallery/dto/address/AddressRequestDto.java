package fdn.fdncargallery.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    // Bina / site adı, bina no ve daire no opsiyoneldir: her adreste bulunmaz.
    @Size(max = 100, message = "Bina / site adı en fazla 100 karakter olabilir.")
    private String buildingName;

    @Size(max = 10, message = "Bina no en fazla 10 karakter olabilir.")
    private String buildingNo;

    @Size(max = 10, message = "Daire no en fazla 10 karakter olabilir.")
    private String doorNo;

    @Pattern(regexp = "^$|^\\d{5}$", message = "Posta kodu 5 haneli olmalıdır.")
    private String zipCode;
}
