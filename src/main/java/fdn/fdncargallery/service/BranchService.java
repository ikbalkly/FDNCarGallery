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
import java.util.Objects;

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
        log.info("Yeni şube oluşturuldu. id: {}, şube: {}", savedBranch.getId(), savedBranch.getBranchName());
        return branchMapper.toResponse(savedBranch);
    }

    @Transactional
    @Override
    public BranchResponseDto updateBranch(UpdateBranchRequestDto updateBranchRequestDto, Long id) {

        // Şube admini yalnızca KENDİ şubesini güncelleyebilir.
        securityService.checkBranchAccess(id);

        Branch existingBranch = getBranchEntityById(id);
        Long oldManagerId = existingBranch.getManager() != null ? existingBranch.getManager().getId() : null;

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

        Branch updatedBranch = branchRepository.saveAndFlush(existingBranch);

        Long newManagerId = updatedBranch.getManager() != null ? updatedBranch.getManager().getId() : null;
        if (!Objects.equals(oldManagerId, newManagerId)) {
            log.info("Şube müdürü değiştirildi. şube id: {}, eski müdür id: {}, yeni müdür id: {}", id, oldManagerId, newManagerId);
        }
        log.info("Şube güncellendi. id: {}, şube: {}", updatedBranch.getId(), updatedBranch.getBranchName());
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
        // kalıcı silme: kayıt DB'den gidiyor, şube adı sadece bu satırda kalır
        log.info("Şube silindi. id: {}, şube: {}", id, branch.getBranchName());
    }

    @Transactional
    @Override
    public Branch getBranchEntityById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND, id.toString())));
    }
}
