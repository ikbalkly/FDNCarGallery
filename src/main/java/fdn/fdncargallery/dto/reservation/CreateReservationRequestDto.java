package fdn.fdncargallery.dto.reservation;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationRequestDto {

    @NotNull(message = "Rezerve edilecek stok kalemi seçilmelidir!")
    private Long stockItemId;

    @NotNull(message = "Rezervasyonu yapan müşteri seçilmelidir!")
    private Long customerId;

    @NotNull(message = "Rezervasyon bitiş tarihi boş geçilemez!")
    @Future(message = "Rezervasyon bitiş tarihi gelecekte olmalıdır!")
    private LocalDateTime expirationDate;

    // Kaporasız rezervasyon mümkün olduğu için @Positive değil @PositiveOrZero
    @NotNull(message = "Kapora boş geçilemez!")
    @PositiveOrZero(message = "Kapora negatif olamaz!")
    @Digits(integer = 13, fraction = 2, message = "Kapora en fazla 2 ondalık basamak içerebilir!")
    private BigDecimal depositAmount;

    @Size(max = 500, message = "Not en fazla 500 karakter olabilir!")
    private String note;
}
