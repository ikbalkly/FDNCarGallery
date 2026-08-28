package fdn.fdncargallery.dto.stockItem;

import fdn.fdncargallery.dto.BaseEntityResponseDto;
import fdn.fdncargallery.dto.vehicle.VehicleResponseDto;
import fdn.fdncargallery.enums.CarCondition;
import fdn.fdncargallery.enums.CarStatus;
import fdn.fdncargallery.enums.WarrantyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StockItemResponseDto extends BaseEntityResponseDto {

    // Aracın değişmez kimliği
    private VehicleResponseDto vehicle;

    // Bu döngüye ait bilgiler
    private String plateNumber;
    private Integer mileage;
    private String color;
    private BigDecimal listPrice;

    private CarStatus status;
    private CarCondition condition;
    private WarrantyType warrantyType;

    private LocalDate acquiredAt;
    private LocalDate soldAt;

    // Şube özeti
    private Long branchId;
    private String branchName;

    // Stoğa girişi yapan personel
    private Long employeeId;
    private String employeeFullName;
}