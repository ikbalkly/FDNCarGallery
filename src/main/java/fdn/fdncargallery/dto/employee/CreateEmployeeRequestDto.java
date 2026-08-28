package fdn.fdncargallery.dto.employee;

import fdn.fdncargallery.dto.address.AddressRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class CreateEmployeeRequestDto {
    @NotBlank(message = "Ad alanı boş bırakılamaz")
    @Size(min = 2, max = 50, message = "Ad en az 2, en fazla 50 karakter olmalıdır")
    private String name;

    @NotBlank(message = "Soyad alanı boş bırakılamaz")
    @Size(min = 2, max = 50, message = "Soyad en az 2, en fazla 50 karakter olmalıdır")
    private String surname;

    @NotBlank(message = "TC kimlik numarası zorunludur")
    @Pattern(regexp = "^[1-9][0-9]{10}$", message = "TC kimlik numarası 11 haneli olmalıdır")
    private String identityNumber;

    @NotBlank(message = "Telefon numarası zorunludur")
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "Geçerli bir telefon numarası giriniz")
    private String phoneNumber;

    @NotNull(message = "Sabit maaş bilgisi zorunludur")
    @Positive(message = "Maaş sıfırdan büyük olmalıdır")
    @Digits(integer = 13, fraction = 2, message = "Maaş en fazla 2 ondalık basamak içerebilir")
    private BigDecimal baseSalary;

    @NotNull(message = "Personelin çalışacağı şube seçilmelidir")
    private Long branchId;

    @Valid
    @NotNull(message = "Personel adresi zorunludur")
    private AddressRequestDto address;

    @NotBlank(message = "E-posta adresi zorunludur")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

}
