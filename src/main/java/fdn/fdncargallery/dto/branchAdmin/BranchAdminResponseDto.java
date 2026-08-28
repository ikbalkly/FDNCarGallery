package fdn.fdncargallery.dto.branchAdmin;

import com.fasterxml.jackson.annotation.JsonInclude;
import fdn.fdncargallery.dto.employee.EmployeeResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class BranchAdminResponseDto extends EmployeeResponseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String temporaryPassword;
}
