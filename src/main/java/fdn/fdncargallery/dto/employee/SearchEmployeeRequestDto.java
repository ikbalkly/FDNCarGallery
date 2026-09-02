package fdn.fdncargallery.dto.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchEmployeeRequestDto {

    @NotBlank(message = "TC kimlik numarası zorunludur")
    @Pattern(regexp = "^[1-9][0-9]{10}$", message = "TC kimlik numarası 11 haneli olmalıdır")
    private String identityNumber;
}