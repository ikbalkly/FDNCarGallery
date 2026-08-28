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
public class CreateCarMaintenanceRequestDto {

    @NotNull(message = "Bakıma gidecek stok kalemi (Stock Item ID) seçilmelidir")
    private Long stockItemId;

    @NotBlank(message = "Bakım/Ekspertiz açıklaması boş bırakılamaz")
    private String description;

    @NotNull(message = "Bakım tipi belirtilmelidir")
    private MaintenanceType maintenanceType;

    @NotNull(message = "Bakım başlangıç tarihi zorunludur")
    private LocalDate startDate;

    @NotNull(message = "Tahmini veya kesinleşmiş bakım ücreti zorunludur")
    @PositiveOrZero(message = "Bakım ücreti negatif olamaz")
    private BigDecimal cost;
}
