package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.IEmployeeController;
import fdn.fdncargallery.dto.employee.EmployeeSearchResultDto;
import fdn.fdncargallery.dto.employee.SearchEmployeeRequestDto;
import fdn.fdncargallery.service.interfaces.IEmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN')")
public class EmployeeController implements IEmployeeController {

    private final IEmployeeService employeeService;

    @PostMapping("/search_employee")
    public ResponseEntity<EmployeeSearchResultDto> findEmployeeByIdentityNumber(@Valid @RequestBody SearchEmployeeRequestDto searchEmployeeRequestDto) {
        return ResponseEntity.ok(employeeService.findEmployeeByIdentityNumber(searchEmployeeRequestDto));
    }
}
