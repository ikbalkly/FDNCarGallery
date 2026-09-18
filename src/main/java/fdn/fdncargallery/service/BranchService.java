package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.branch.BranchResponseDto;
import fdn.fdncargallery.dto.branch.CreateBranchRequestDto;
import fdn.fdncargallery.dto.branch.UpdateBranchRequestDto;
import fdn.fdncargallery.entity.Address;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.Branch;
import fdn.fdncargallery.entity.Manager;
import fdn.fdncargallery.entity.StockItem;
import fdn.fdncargallery.enums.CarStatus;
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
                ? branchRepository.findAllByDeletedAtIsNull()
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

        // id ile gelen branchte çalışan active employee var mı?
        boolean hasActiveEmployee = false;
        if (branch.getEmployees() != null) {
            for (BaseEmployee employee : branch.getEmployees()) {
                if (employee.isActive()) {
                    hasActiveEmployee = true;
                    break;
                }
            }
        }

        // active personel varsa error fırlatır
        if (hasActiveEmployee) {
            throw new BaseException(new ErrorMessage(MessageType.BRANCH_HAS_EMPLOYEES, branch.getBranchName()));
        }

        // şubede hala araba durumu sold olmayan kayıt var mı?
        boolean hasUnsoldStock = false;
        if (branch.getStockItems() != null) {
            for (StockItem stockItem : branch.getStockItems()) {
                if (!stockItem.isDeleted() && stockItem.getStatus() != CarStatus.SOLD) {
                    hasUnsoldStock = true;
                    break;
                }
            }
        }
        if (hasUnsoldStock) {
            throw new BaseException(new ErrorMessage(MessageType.BRANCH_HAS_STOCK, branch.getBranchName()));
        }

        // soft delete yapılır
        branch.softDelete(securityService.getCurrentEmployee());
        branchRepository.saveAndFlush(branch);

        log.info("Şube kapatıldı. id: {}, şube: {}", id, branch.getBranchName());
    }

    @Transactional
    @Override
    public Branch getBranchEntityById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND, id.toString())));

        if (branch.isDeleted()) {
            throw new BaseException(new ErrorMessage(MessageType.BRANCH_NOT_FOUND, id.toString()));
        }
        return branch;
    }
}
