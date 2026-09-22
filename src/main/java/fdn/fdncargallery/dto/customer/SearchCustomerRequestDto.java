package fdn.fdncargallery.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCustomerRequestDto {

    @NotBlank(message = "Kimlik / Vergi numarası boş bırakılamaz")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Kimlik numarası 11 (TCKN) veya 10 (VKN) haneli olmalıdır")
    private String identityNumber;
}
