package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.IBranchController;
import fdn.fdncargallery.dto.branch.BranchResponseDto;
import fdn.fdncargallery.dto.branch.CreateBranchRequestDto;
import fdn.fdncargallery.dto.branch.UpdateBranchRequestDto;
import fdn.fdncargallery.service.interfaces.IBranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor

@PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
public class BranchController implements IBranchController {

    private final IBranchService branchService;

    @PostMapping("/create_branch")
    public ResponseEntity<BranchResponseDto> createBranch(@Valid @RequestBody CreateBranchRequestDto createBranchRequestDto) {
        BranchResponseDto response = branchService.createBranch(createBranchRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update_branch/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'BRANCH_ADMIN')")
    public ResponseEntity<BranchResponseDto> updateBranch(@Valid @RequestBody UpdateBranchRequestDto updateBranchRequestDto,
                                                          @PathVariable Long id) {
        return ResponseEntity.ok(branchService.updateBranch(updateBranchRequestDto, id));
    }

    @GetMapping("/list_branch/{id}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'BRANCH_ADMIN', 'MANAGER')")
    public ResponseEntity<BranchResponseDto> findBranchById(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.findBranchById(id));
    }

    @GetMapping("/list_branch")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'BRANCH_ADMIN', 'MANAGER')")
    public ResponseEntity<List<BranchResponseDto>> findAllBranches() {
        return ResponseEntity.ok(branchService.findAllBranches());
    }

    @DeleteMapping("/delete_branch/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }
}
