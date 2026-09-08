package fdn.fdncargallery.dto.reservation;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelReservationRequestDto {

    // Reservation.cancel(String reason) metodunun HTTP karşılığı.
    // markExpired() ve markConverted() için ayrı bir DTO yoktur: bunlar
    // istemciden değil, zamanlanmış görevden ve satış akışından tetiklenir.

    @NotBlank(message = "İptal gerekçesi boş geçilemez!")
    @Size(max = 500, message = "İptal gerekçesi en fazla 500 karakter olabilir!")
    private String reason;
}
