package fdn.fdncargallery.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import fdn.fdncargallery.dto.BaseEntityResponseDto;
import fdn.fdncargallery.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReservationResponseDto extends BaseEntityResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime reservationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime expirationDate;

    private BigDecimal depositAmount;
    private ReservationStatus status;

    // Not veya iptal gerekçesi
    private String note;

    // Entity'den türetilir, ayrı kolon değildir.
    // İkisi birden gerekli: status ACTIVE iken de saat geçmiş olabilir
    // (süresi dolmuş ama henüz EXPIRED işaretlenmemiş kayıt).
    private boolean active;
    private boolean expired;

    // --- Stok kalemi ve araç özeti ---
    private Long stockItemId;
    private String plateNumber;
    private String brandAndModel;
    private String vin;

    // --- Rezervasyonu yapan müşteri ---
    private Long customerId;
    private String customerFullName;
    private String customerIdentityNumber;

    // --- Rezervasyonu açan personel ---
    private Long employeeId;
    private String employeeFullName;
}
