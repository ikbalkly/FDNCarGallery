package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.IManagerController;
import fdn.fdncargallery.dto.employee.ReactivateEmployeeRequestDto;
import fdn.fdncargallery.dto.manager.CreateManagerRequestDto;
import fdn.fdncargallery.dto.manager.ManagerResponseDto;
import fdn.fdncargallery.dto.manager.UpdateManagerRequestDto;
import fdn.fdncargallery.service.interfaces.IManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN')")
public class ManagerController implements IManagerController {

    private final IManagerService managerService;

    @PostMapping("/create_manager")
    public ResponseEntity<ManagerResponseDto> createManager(@Valid @RequestBody CreateManagerRequestDto createManagerRequestDto) {
        ManagerResponseDto response = managerService.createManager(createManagerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update_manager/{id}")
    public ResponseEntity<ManagerResponseDto> updateManager(@Valid @RequestBody UpdateManagerRequestDto updateManagerRequestDto,
                                                            @PathVariable Long id) {
        return ResponseEntity.ok(managerService.updateManager(updateManagerRequestDto, id));
    }

    @GetMapping("/list_manager/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN', 'MANAGER')")
    public ResponseEntity<ManagerResponseDto> findManagerById(@PathVariable Long id) {
        return ResponseEntity.ok(managerService.findManagerById(id));
    }

    @GetMapping("/list_manager")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN', 'MANAGER')")
    public ResponseEntity<List<ManagerResponseDto>> findAllManagers() {
        return ResponseEntity.ok(managerService.findAllManagers());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteManager(@PathVariable Long id) {
        managerService.deleteManager(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reactivate_manager/{id}")
    public ResponseEntity<ManagerResponseDto> reactivateManager(@Valid @RequestBody ReactivateEmployeeRequestDto reactivateEmployeeRequestDto,
                                                                @PathVariable Long id) {
        return ResponseEntity.ok(managerService.reactivateManager(reactivateEmployeeRequestDto, id));
    }
}
