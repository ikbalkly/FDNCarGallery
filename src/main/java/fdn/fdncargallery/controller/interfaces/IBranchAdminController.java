package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.branchAdmin.BranchAdminResponseDto;
import fdn.fdncargallery.dto.branchAdmin.CreateBranchAdminRequestDto;
import fdn.fdncargallery.dto.branchAdmin.UpdateBranchAdminRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBranchAdminController {

    ResponseEntity<BranchAdminResponseDto> createBranchAdmin(CreateBranchAdminRequestDto createBranchAdminRequestDto);

    ResponseEntity<BranchAdminResponseDto> updateBranchAdmin(UpdateBranchAdminRequestDto updateBranchAdminRequestDto, Long id);

    ResponseEntity<BranchAdminResponseDto> findBranchAdminById(Long id);

    ResponseEntity<List<BranchAdminResponseDto>> findAllBranchAdmins();

    ResponseEntity<Void> deleteBranchAdmin(Long id);
}
