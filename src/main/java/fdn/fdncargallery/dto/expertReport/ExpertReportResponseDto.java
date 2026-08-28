package fdn.fdncargallery.dto.expertReport;

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
public class ExpertReportResponseDto extends BaseEntityResponseDto {

    private String reportCompany;
    private LocalDate reportDate;

    private String enginePerformancePercentage;
    private boolean hasHeavyDamageRecord;
    private BigDecimal tramerTotalAmount;
    private String expertDetails;

    private Long stockItemId;
    private String plateNumber;
    private String brandAndModel;
    private String vin;
}
