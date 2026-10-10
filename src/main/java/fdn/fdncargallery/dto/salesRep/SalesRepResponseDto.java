package fdn.fdncargallery.dto.salesRep;

import fdn.fdncargallery.dto.employee.EmployeeResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SalesRepResponseDto extends EmployeeResponseDto {

    private Long monthlySalesCount;
}
