package fdn.fdncargallery.dto.expertReport;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExpertReportRequestDto {

    @NotNull(message = "Ekspertiz yapılacak stok kalemi (Stock Item ID) seçilmelidir")
    private Long stockItemId;

    @NotBlank(message = "Ekspertiz firması boş bırakılamaz")
    @Size(min = 2, max = 100, message = "Firma adı en az 2, en fazla 100 karakter olmalıdır")
    private String reportCompany;

    @NotNull(message = "Rapor tarihi zorunludur")
    private LocalDate reportDate;

    @NotBlank(message = "Motor performans yüzdesi boş bırakılamaz (Örn: %85)")
    @Pattern(regexp = "^%?[0-9]{1,3} ?%?$", message = "Geçerli bir yüzde giriniz (Örn: %85 veya 85)")
    private String enginePerformancePercentage;

    @NotNull(message = "Ağır hasar kaydı bilgisi zorunludur")
    private Boolean hasHeavyDamageRecord;

    @NotNull(message = "Tramer hasar tutarı zorunludur")
    @PositiveOrZero(message = "Tramer tutarı negatif olamaz")
    private BigDecimal tramerTotalAmount;

    @NotBlank(message = "Ekspertiz detayları (Kaporta, boya vb.) boş bırakılamaz")
    @Size(max = 1000, message = "Açıklama en fazla 1000 karakter olabilir")
    private String expertDetails;
}
