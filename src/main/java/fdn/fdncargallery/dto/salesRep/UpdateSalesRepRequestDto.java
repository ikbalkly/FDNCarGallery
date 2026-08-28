package fdn.fdncargallery.dto.salesRep;

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
public class UpdateSalesRepRequestDto extends UpdateEmployeeRequestDto {
    @NotNull(message = "Komisyon oranı zorunludur")
    @DecimalMin(value = "0.000")
    @DecimalMax(value = "100.000")
    private BigDecimal commissionRate;
}
