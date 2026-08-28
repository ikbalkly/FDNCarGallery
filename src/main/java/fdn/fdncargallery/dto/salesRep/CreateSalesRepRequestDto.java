package fdn.fdncargallery.dto.salesRep;

import fdn.fdncargallery.dto.employee.CreateEmployeeRequestDto;
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
public class CreateSalesRepRequestDto extends CreateEmployeeRequestDto {

    // 0.123 veya 12.345 formatına uygun validasyon
    @NotNull(message = "Komisyon oranı zorunludur")
    @DecimalMin(value = "0.000", message = "Komisyon oranı negatif olamaz")
    @DecimalMax(value = "100.000", message = "Komisyon oranı %100'den büyük olamaz")
    private BigDecimal commissionRate;
}
