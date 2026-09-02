package fdn.fdncargallery.dto.employee;

import fdn.fdncargallery.dto.address.AddressRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactivateEmployeeRequestDto {

    @NotNull(message = "Personelin döneceği şube seçilmelidir")
    private Long branchId;

    @NotNull(message = "Sabit maaş bilgisi zorunludur")
    @Positive(message = "Maaş sıfırdan büyük olmalıdır")
    @Digits(integer = 13, fraction = 2, message = "Maaş en fazla 2 ondalık basamak içerebilir")
    private BigDecimal baseSalary;

    // boş bırakılırsa personelin mevcut e-postası korunur.
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    //boş bırakılırsa yeniden işe alım tarihi bugün kabul edilir.
    @PastOrPresent(message = "İşe giriş tarihi gelecekte olamaz")
    private LocalDate hireDate;

    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "Geçerli bir telefon numarası giriniz")
    private String phoneNumber;

    @Valid
    private AddressRequestDto address;
}
