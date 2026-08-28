package fdn.fdncargallery.dto.carPurchase;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCarPurchaseRequestDto {

    @NotNull(message = "Satın alınan stok kalemi (Stock Item ID) seçilmelidir")
    private Long stockItemId;

    @NotNull(message = "Aracı satan müşteri (Customer ID) seçilmelidir")
    private Long sellerCustomerId;

    @NotNull(message = "Alış tarihi zorunludur")
    @PastOrPresent(message = "Alış tarihi gelecekte olamaz")
    private LocalDate purchaseDate;

    @NotNull(message = "Alış fiyatı zorunludur")
    @Positive(message = "Alış fiyatı sıfırdan büyük olmalıdır")
    @Digits(integer = 13, fraction = 2, message = "Alış fiyatı en fazla 2 ondalık basamak içerebilir")
    private BigDecimal purchasePrice;

    @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    private String purchaseNotes;
}
