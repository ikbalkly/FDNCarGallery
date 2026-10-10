package fdn.fdncargallery.dto.salesRep;

import fdn.fdncargallery.dto.employee.CreateEmployeeRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CreateSalesRepRequestDto extends CreateEmployeeRequestDto {
}
