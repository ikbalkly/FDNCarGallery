package fdn.fdncargallery.dto.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBrandRequestDto {

    @NotBlank(message = "Marka adı boş bırakılamaz (Örn: Ford)")
    @Size(max = 60, message = "Marka adı en fazla 60 karakter olabilir.")
    private String brandName;
}
