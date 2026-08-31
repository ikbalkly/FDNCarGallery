package fdn.fdncargallery.dto.branchAdmin;

import com.fasterxml.jackson.annotation.JsonInclude;
import fdn.fdncargallery.dto.employee.EmployeeResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BranchAdminResponseDto extends EmployeeResponseDto {

}
