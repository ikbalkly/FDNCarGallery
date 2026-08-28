package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.IBranchAdminController;
import fdn.fdncargallery.dto.branchAdmin.BranchAdminResponseDto;
import fdn.fdncargallery.dto.branchAdmin.CreateBranchAdminRequestDto;
import fdn.fdncargallery.dto.branchAdmin.UpdateBranchAdminRequestDto;
import fdn.fdncargallery.service.interfaces.IBranchAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branch-admins")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
public class BranchAdminController implements IBranchAdminController {

    private final IBranchAdminService branchAdminService;

    @PostMapping("/create_branch_admin")
    public ResponseEntity<BranchAdminResponseDto> createBranchAdmin(@Valid @RequestBody CreateBranchAdminRequestDto createBranchAdminRequestDto) {
        BranchAdminResponseDto response = branchAdminService.createBranchAdmin(createBranchAdminRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update_branch_admin/{id}")
    public ResponseEntity<BranchAdminResponseDto> updateBranchAdmin(@Valid @RequestBody UpdateBranchAdminRequestDto updateBranchAdminRequestDto,
                                                                    @PathVariable Long id) {
        return ResponseEntity.ok(branchAdminService.updateBranchAdmin(updateBranchAdminRequestDto, id));
    }

    @GetMapping("/list_branch_admin/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<BranchAdminResponseDto> findBranchAdminById(@PathVariable Long id) {
        return ResponseEntity.ok(branchAdminService.findBranchAdminById(id));
    }

    @GetMapping("/list_branch_admin")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<List<BranchAdminResponseDto>> findAllBranchAdmins() {
        return ResponseEntity.ok(branchAdminService.findAllBranchAdmins());
    }

    @DeleteMapping("/delete_branch_admin/{id}")
    public ResponseEntity<Void> deleteBranchAdmin(@PathVariable Long id) {
        branchAdminService.deleteBranchAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
