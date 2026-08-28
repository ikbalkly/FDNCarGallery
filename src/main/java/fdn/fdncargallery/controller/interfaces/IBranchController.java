package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.branch.BranchResponseDto;
import fdn.fdncargallery.dto.branch.CreateBranchRequestDto;
import fdn.fdncargallery.dto.branch.UpdateBranchRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBranchController {

    public ResponseEntity<BranchResponseDto> createBranch(CreateBranchRequestDto createBranchRequestDto);

    public ResponseEntity<BranchResponseDto> updateBranch(UpdateBranchRequestDto updateBranchRequestDto, Long id);

    public ResponseEntity<BranchResponseDto> findBranchById(Long id);

    public ResponseEntity<List<BranchResponseDto>> findAllBranches();

    public ResponseEntity<Void> deleteBranch(Long id);
}
