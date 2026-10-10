package fdn.fdncargallery.dto.branch;

import fdn.fdncargallery.dto.BaseEntityResponseDto;
import fdn.fdncargallery.dto.address.AddressResponseDto;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BranchResponseDto extends BaseEntityResponseDto {

    private String branchName;

    private AddressResponseDto address;

    private Long managerId;
    private String managerFullName;

    private int totalCars;
    private int totalEmployees;

    private BigDecimal commissionRate;
    private Integer monthlySalesTarget;
    private BigDecimal targetBonusPerCar;
}
