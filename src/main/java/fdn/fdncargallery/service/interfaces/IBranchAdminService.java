package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.branchAdmin.BranchAdminResponseDto;
import fdn.fdncargallery.dto.branchAdmin.CreateBranchAdminRequestDto;
import fdn.fdncargallery.dto.branchAdmin.UpdateBranchAdminRequestDto;
import fdn.fdncargallery.entity.SystemAdmin;

import java.util.List;

public interface IBranchAdminService {

    BranchAdminResponseDto createBranchAdmin(CreateBranchAdminRequestDto createBranchAdminRequestDto);

    BranchAdminResponseDto updateBranchAdmin(UpdateBranchAdminRequestDto updateBranchAdminRequestDto, Long id);

    BranchAdminResponseDto findBranchAdminById(Long id);

    List<BranchAdminResponseDto> findAllBranchAdmins();

    void deleteBranchAdmin(Long id);

    SystemAdmin getBranchAdminEntityById(Long id);
}
