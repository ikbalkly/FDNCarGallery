package fdn.fdncargallery.dto.carPurchase;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCarPurchaseRequestDto {

    @NotNull(message = "Alış fiyatı zorunludur")
    @Positive(message = "Alış fiyatı sıfırdan büyük olmalıdır")
    private BigDecimal purchasePrice;

    @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    private String purchaseNotes;
}
