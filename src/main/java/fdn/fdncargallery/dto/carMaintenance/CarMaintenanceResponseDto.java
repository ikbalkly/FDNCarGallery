package fdn.fdncargallery.dto.carMaintenance;

import fdn.fdncargallery.dto.BaseEntityResponseDto;
import fdn.fdncargallery.enums.MaintenanceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CarMaintenanceResponseDto extends BaseEntityResponseDto {

    private String description;
    private MaintenanceType maintenanceType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal cost;

    // endDate'ten türetilir, ayrı bir kolon değildir
    private boolean completed;

    // --- Stok kalemi ve araç özeti ---
    private Long stockItemId;
    private String plateNumber;
    private String brandAndModel;

    // --- Bakımı açan personel ---
    private Long employeeId;
    private String employeeFullName;
}
