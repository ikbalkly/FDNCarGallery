package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.branch.BranchResponseDto;
import fdn.fdncargallery.dto.branch.CreateBranchRequestDto;
import fdn.fdncargallery.dto.branch.UpdateBranchRequestDto;
import fdn.fdncargallery.entity.Address;
import fdn.fdncargallery.entity.Branch;
import fdn.fdncargallery.entity.Manager;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IBranchMapper;
import fdn.fdncargallery.repository.IBranchRepository;
import fdn.fdncargallery.repository.IManagerRepository;
import fdn.fdncargallery.service.interfaces.IBranchService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchService implements IBranchService {

    private final IBranchRepository branchRepository;
    private final IBranchMapper branchMapper;
    private final IManagerRepository managerRepository;
    private final SecurityService securityService;

    @Transactional
    @Override
    public List<BranchResponseDto> findAllBranches() {
        // super tüm şubeleri görür. ADMIN ve MANAGER yalnızca kendi şubesini
        List<Branch> branches = securityService.isSuperAdmin()
                ? branchRepository.findAll()
                : List.of(getBranchEntityById(securityService.getCurrentBranchId()));

        List<BranchResponseDto> responseDtoArrayList = new ArrayList<>();
        for (Branch branch : branches) {
            responseDtoArrayList.add(branchMapper.toResponse(branch));
        }
        return responseDtoArrayList;
    }

    @Transactional
    @Override
    public BranchResponseDto findBranchById(Long id) {
        securityService.checkBranchAccess(id);
        return branchMapper.toResponse(getBranchEntityById(id));
    }

    @Transactional
    @Override
    public BranchResponseDto createBranch(CreateBranchRequestDto createBranchRequestDto) {

        if (branchRepository.existsByBranchName(createBranchRequestDto.getBranchName())) {
            throw new BaseException(new ErrorMessage(MessageType.ALREADY_EXISTS, createBranchRequestDto.getBranchName()));
        }

        Branch branch = branchMapper.toEntity(createBranchRequestDto);

        Branch savedBranch = branchRepository.saveAndFlush(branch);
        return branchMapper.toResponse(savedBranch);
    }

    @Transactional
    @Override
    public BranchResponseDto updateBranch(UpdateBranchRequestDto updateBranchRequestDto, Long id) {

        // Şube admini yalnızca KENDİ şubesini güncelleyebilir.
        securityService.checkBranchAccess(id);

        Branch existingBranch = getBranchEntityById(id);

        if (!existingBranch.getBranchName().equals(updateBranchRequestDto.getBranchName())
                && branchRepository.existsByBranchName(updateBranchRequestDto.getBranchName())) {
            throw new BaseException(new ErrorMessage(MessageType.ALREADY_EXISTS, updateBranchRequestDto.getBranchName()));
        }

        // managerId GÖNDERİLDİYSE müdür değiştirilir, gönderilmediyse mevcut müdüre DOKUNULMAZ.
        if (updateBranchRequestDto.getManagerId() != null) {

            Manager manager = managerRepository.findById(updateBranchRequestDto.getManagerId())
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.MANAGER_NOT_FOUND, updateBranchRequestDto.getManagerId().toString())));

            // müdür bu şubede çalışıyor olmalı
            if (manager.getBranch() == null || !manager.getBranch().getId().equals(id)) {
                throw new BaseException(new ErrorMessage(MessageType.EMPLOYEE_NOT_IN_BRANCH, manager.getId().toString()));
            }

            // başka bir şubenin müdürü olamaz
            branchRepository.findByManagerId(manager.getId()).ifPresent(otherBranch -> {
                if (!otherBranch.getId().equals(id)) {
                    throw new BaseException(new ErrorMessage(MessageType.MANAGER_ALREADY_ASSIGNED, otherBranch.getBranchName()));
                }
            });

            existingBranch.setManager(manager);
        }

        // branchName ve address yerinde güncellenir
        branchMapper.updateBranchFromDto(updateBranchRequestDto, existingBranch);

        Branch updatedBranch = branchRepository.save(existingBranch);
        return branchMapper.toResponse(updatedBranch);
    }

    @Transactional
    @Override
    public void deleteBranch(Long id) {
        Branch branch = getBranchEntityById(id);

        if (branch.getEmployees() != null && !branch.getEmployees().isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.DATA_INTEGRITY_VIOLATION,
                    "Şubede kayıtlı personel var, önce personelleri başka şubeye taşıyın."));
        }
        if (branch.getStockItems() != null && !branch.getStockItems().isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.DATA_INTEGRITY_VIOLATION,
                    "Şubede kayıtlı araç var, önce araçları başka şubeye taşıyın."));
        }

        branchRepository.delete(branch);
        log.info("Şube silindi. id: {}", id);
    }

    @Transactional
    @Override
    public Branch getBranchEntityById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND, id.toString())));
    }
}
