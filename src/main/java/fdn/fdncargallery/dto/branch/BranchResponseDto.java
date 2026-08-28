package fdn.fdncargallery.dto.branch;

import fdn.fdncargallery.dto.BaseEntityResponseDto;
import fdn.fdncargallery.dto.address.AddressResponseDto;
import lombok.*;

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
}
