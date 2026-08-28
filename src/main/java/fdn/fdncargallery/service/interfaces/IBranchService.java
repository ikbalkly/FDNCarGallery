package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.branch.BranchResponseDto;
import fdn.fdncargallery.dto.branch.CreateBranchRequestDto;
import fdn.fdncargallery.dto.branch.UpdateBranchRequestDto;
import fdn.fdncargallery.entity.Branch;

import java.util.List;

public interface IBranchService {
    public List<BranchResponseDto> findAllBranches();

    public BranchResponseDto findBranchById(Long id);

    public BranchResponseDto createBranch(CreateBranchRequestDto createBranchRequestDto);

    public BranchResponseDto updateBranch(UpdateBranchRequestDto updateBranchRequestDto, Long id);

    public void deleteBranch(Long id);

    public Branch getBranchEntityById(Long id);
}
