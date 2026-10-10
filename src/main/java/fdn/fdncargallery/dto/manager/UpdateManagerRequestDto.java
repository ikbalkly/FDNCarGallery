package fdn.fdncargallery.dto.manager;

import fdn.fdncargallery.dto.employee.UpdateEmployeeRequestDto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateManagerRequestDto extends UpdateEmployeeRequestDto {

    @NotNull(message = "Maksimum indirim oranı zorunludur")
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "100.00")
    private BigDecimal maxDiscountRate;
}