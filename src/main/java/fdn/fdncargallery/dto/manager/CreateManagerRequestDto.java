package fdn.fdncargallery.dto.manager;

import fdn.fdncargallery.dto.employee.CreateEmployeeRequestDto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CreateManagerRequestDto extends CreateEmployeeRequestDto {

    @NotNull(message = "Müdürün yapabileceği maksimum indirim oranı zorunludur")
    @DecimalMin(value = "0.00", message = "İndirim oranı 0'dan küçük olamaz")
    @DecimalMax(value = "100.00", message = "İndirim oranı %100'den büyük olamaz")
    private BigDecimal maxDiscountRate;

    @NotNull(message = "Şubenin aylık satış hedefi zorunludur")
    @PositiveOrZero(message = "Satış hedefi negatif olamaz")
    private BigDecimal branchMonthlySalesTarget;

    @NotNull(message = "Yönetim primi (Bonus) zorunludur")
    @PositiveOrZero(message = "Bonus miktarı negatif olamaz")
    private BigDecimal managementBonus;
}
