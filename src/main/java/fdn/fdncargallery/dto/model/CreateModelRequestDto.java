package fdn.fdncargallery.dto.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateModelRequestDto {

    @NotNull(message = "Marka id'si zorunludur")
    private Long brandId;

    @NotBlank(message = "Model adı boş bırakılamaz (Örn: Focus)")
    @Size(max = 60, message = "Model adı en fazla 60 karakter olabilir.")
    private String modelName;
}
