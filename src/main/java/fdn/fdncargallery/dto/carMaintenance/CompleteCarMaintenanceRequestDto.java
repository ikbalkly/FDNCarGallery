package fdn.fdncargallery.dto.carMaintenance;

import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteCarMaintenanceRequestDto {

    @PastOrPresent(message = "Teslim tarihi ileri bir tarih olamaz")
    private LocalDate completedAt;
}
