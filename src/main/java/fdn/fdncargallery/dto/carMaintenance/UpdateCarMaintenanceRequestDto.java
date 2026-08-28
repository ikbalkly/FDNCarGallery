package fdn.fdncargallery.dto.carMaintenance;


import fdn.fdncargallery.enums.MaintenanceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCarMaintenanceRequestDto {

    @NotBlank(message = "Bakım/Ekspertiz açıklaması boş bırakılamaz")
    private String description;

    @NotNull(message = "Bakım tipi belirtilmelidir")
    private MaintenanceType maintenanceType;

    // Bitiş tarihi girilmesi bakımın kapatılması demektir (completed = endDate != null)
    @NotNull(message = "Bakım bitiş tarihi zorunludur")
    private LocalDate endDate;

    @NotNull(message = "Kesinleşen bakım ücreti zorunludur")
    @PositiveOrZero(message = "Bakım ücreti negatif olamaz")
    private BigDecimal finalCost;
}
