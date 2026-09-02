package fdn.fdncargallery.dto.employee;

import fdn.fdncargallery.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSearchResultDto {

    private Long id;
    private String name;
    private String surname;
    private Role role;
    private boolean active;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private Long branchId;
    private String branchName;
}