package fdn.fdncargallery.dto.soldCar;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSoldCarRequestDto {

    @NotNull(message = "Nihai satış fiyatı zorunludur")
    @Positive(message = "Satış fiyatı sıfırdan büyük olmalıdır")
    private BigDecimal salePrice;
}
