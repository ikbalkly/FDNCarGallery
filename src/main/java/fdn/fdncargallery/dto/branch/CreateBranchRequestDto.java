package fdn.fdncargallery.dto.branch;

import fdn.fdncargallery.dto.address.AddressRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBranchRequestDto {

    @NotBlank(message = "Şube adı boş bırakılamaz!")
    private String branchName;

    @Valid
    @NotNull(message = "Şube adresi zorunludur!")
    private AddressRequestDto address;

    @DecimalMin(value = "0.000", message = "Komisyon oranı negatif olamaz")
    @DecimalMax(value = "1.000", message = "Komisyon oranı %1'den büyük olamaz")
    private BigDecimal commissionRate;

    @Min(value = 1, message = "Aylık satış hedefi en az 1 araç olmalıdır")
    private Integer monthlySalesTarget;

    @DecimalMin(value = "0.00", message = "Hedef primi negatif olamaz")
    private BigDecimal targetBonusPerCar;
}
