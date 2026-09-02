package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.employee.EmployeeSearchResultDto;
import fdn.fdncargallery.dto.employee.SearchEmployeeRequestDto;
import org.springframework.http.ResponseEntity;

public interface IEmployeeController {
    ResponseEntity<EmployeeSearchResultDto> findEmployeeByIdentityNumber(SearchEmployeeRequestDto searchEmployeeRequestDto);
}
