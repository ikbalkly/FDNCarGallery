package fdn.fdncargallery.dto.customer;

import fdn.fdncargallery.dto.address.AddressRequestDto;
import fdn.fdncargallery.enums.CustomerType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerRequestDto {

    @NotNull(message = "Satılan stok kalemi (Stock Item ID) seçilmelidir")
    private Long stockItemId;

    @NotBlank(message = "Kimlik / Vergi numarası boş bırakılamaz")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Kimlik numarası 11 (TCKN) veya 10 (VKN) haneli olmalıdır")
    private String identityNumber;

    @NotBlank(message = "Ad alanı boş bırakılamaz")
    @Size(min = 2, max = 50, message = "Ad en az 2, en fazla 50 karakter olmalıdır")
    private String firstName;

    @NotBlank(message = "Soyad alanı boş bırakılamaz")
    @Size(min = 2, max = 50, message = "Soyad en az 2, en fazla 50 karakter olmalıdır")
    private String lastName;

    @NotBlank(message = "Telefon numarası zorunludur")
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "Geçerli bir telefon numarası giriniz (Örn: 5551234567)")
    private String phoneNumber;

    @Email(message = "Lütfen geçerli bir email adresi giriniz")
    private String email;

    @Valid
    @NotNull(message = "Müşteri adresi zorunludur")
    private AddressRequestDto address;
}
