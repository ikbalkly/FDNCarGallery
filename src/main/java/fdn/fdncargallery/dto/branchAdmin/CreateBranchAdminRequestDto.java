package fdn.fdncargallery.dto.branchAdmin;

import fdn.fdncargallery.dto.employee.CreateEmployeeRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CreateBranchAdminRequestDto extends CreateEmployeeRequestDto {
}
