package fdn.fdncargallery.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResendPasswordResultDto {

    private Long employeeId;

    private String email;
}