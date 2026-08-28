package fdn.fdncargallery.dto.expertReport;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateExpertReportRequestDto {

    @NotBlank(message = "Ekspertiz firması boş bırakılamaz")
    private String reportCompany;

    @NotNull(message = "Rapor tarihi zorunludur")
    private LocalDate reportDate;

    @NotBlank(message = "Motor performans yüzdesi boş bırakılamaz")
    private String enginePerformancePercentage;

    @NotNull(message = "Ağır hasar kaydı bilgisi zorunludur")
    private Boolean hasHeavyDamageRecord;

    @NotNull(message = "Tramer hasar tutarı zorunludur")
    @PositiveOrZero
    private BigDecimal tramerTotalAmount;

    @Size(max = 1000, message = "Açıklama en fazla 1000 karakter olabilir")
    private String expertDetails;
}
