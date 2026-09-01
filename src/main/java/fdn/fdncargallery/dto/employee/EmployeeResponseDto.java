package fdn.fdncargallery.dto.employee;

import fdn.fdncargallery.dto.BaseEntityResponseDto;
import fdn.fdncargallery.dto.address.AddressResponseDto;
import fdn.fdncargallery.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmployeeResponseDto extends BaseEntityResponseDto {

    private String name;
    private String surname;
    private String identityNumber;
    private String phoneNumber;
    private BigDecimal baseSalary;

    private AddressResponseDto address;

    private Long branchId;
    private String branchName;

    private String username;
    private String email;
    private Role role;
    private boolean firstLogin;
    private boolean active;

    private LocalDate hireDate;
    private LocalDate terminationDate;

    public String getFullName() {
        return name + " " + surname;
    }
}
