package fdn.fdncargallery.dto.soldCar;

import com.fasterxml.jackson.annotation.JsonFormat;
import fdn.fdncargallery.dto.BaseEntityResponseDto;
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
public class SoldCarResponseDto extends BaseEntityResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime saleDate;
    private BigDecimal salePrice;

    // Satış anında dondurulan prim oranı
    private BigDecimal commissionRate;

    private Long stockItemId;
    private String plateNumber;
    private String brandAndModel;
    private String vin;

    private Long customerId;
    private String customerFullName;
    private String customerIdentityNumber;

    private Long employeeId;
    private String employeeFullName;
}
