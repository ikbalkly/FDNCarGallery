package fdn.fdncargallery.dto.stockItem;

import fdn.fdncargallery.enums.CarCondition;
import fdn.fdncargallery.enums.WarrantyType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStockItemRequestDto {

    private Long branchId;

    @NotBlank(message = "Plaka zorunludur")
    @Pattern(regexp = "^(0[1-9]|[1-7][0-9]|8[0-1])[A-Z]{1,3}\\d{2,4}$", message = "Geçerli bir Türkiye plakası giriniz (Boşluksuz)")
    private String plateNumber;

    @NotNull(message = "Kilometre bilgisi zorunludur")
    @Min(value = 0, message = "Kilometre negatif olamaz")
    private Integer mileage;

    @NotBlank(message = "Renk alanı boş bırakılamaz")
    private String color;

    @NotNull(message = "Liste fiyatı zorunludur")
    @Positive(message = "Fiyat sıfırdan büyük olmalıdır")
    @Digits(integer = 13, fraction = 2, message = "Fiyat en fazla 2 ondalık basamak içerebilir")
    private BigDecimal listPrice;

    @NotNull(message = "Araç kondisyonu (Sıfır/İkinci El) belirtilmelidir")
    private CarCondition condition;

    @NotNull(message = "Garanti durumu belirtilmelidir")
    private WarrantyType warrantyType;
}
