package fdn.fdncargallery.dto.salesRep;

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
public class SalesRepResponseDto extends EmployeeResponseDto {

    private BigDecimal commissionRate;
    private Long monthlySalesCount;
}
