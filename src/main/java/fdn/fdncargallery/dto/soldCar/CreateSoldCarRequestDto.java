package fdn.fdncargallery.dto.soldCar;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSoldCarRequestDto {

    @NotNull(message = "Satılan stok kalemi (Stock Item ID) seçilmelidir")
    private Long stockItemId;

    @NotNull(message = "Aracı satın alan müşteri (Customer ID) seçilmelidir")
    private Long customerId;

    @NotNull(message = "Nihai satış fiyatı zorunludur")
    @Positive(message = "Satış fiyatı sıfırdan büyük olmalıdır")
    @Digits(integer = 13, fraction = 2, message = "Satış fiyatı en fazla 2 ondalık basamak içerebilir")
    private BigDecimal salePrice;
}
