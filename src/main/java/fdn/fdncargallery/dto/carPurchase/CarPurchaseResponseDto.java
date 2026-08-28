package fdn.fdncargallery.dto.carPurchase;

import fdn.fdncargallery.dto.BaseEntityResponseDto;
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
public class CarPurchaseResponseDto extends BaseEntityResponseDto {

    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private String purchaseNotes;

    private Long stockItemId;
    private String plateNumber;
    private String brandAndModel;
    private String vin;

    private Long sellerCustomerId;
    private String sellerCustomerFullName;
    private String sellerIdentityNumber;

    private Long employeeId;
    private String employeeFullName;
}
