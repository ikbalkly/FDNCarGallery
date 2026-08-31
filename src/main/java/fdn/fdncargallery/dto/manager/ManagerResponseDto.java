package fdn.fdncargallery.dto.manager;

import com.fasterxml.jackson.annotation.JsonInclude;
import fdn.fdncargallery.dto.employee.EmployeeResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ManagerResponseDto extends EmployeeResponseDto {
    private BigDecimal maxDiscountRate;
    private BigDecimal branchMonthlySalesTarget;
    private BigDecimal managementBonus;

}
