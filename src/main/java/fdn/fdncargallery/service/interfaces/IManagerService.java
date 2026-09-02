package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.employee.ReactivateEmployeeRequestDto;
import fdn.fdncargallery.dto.manager.CreateManagerRequestDto;
import fdn.fdncargallery.dto.manager.ManagerResponseDto;
import fdn.fdncargallery.dto.manager.UpdateManagerRequestDto;
import fdn.fdncargallery.entity.Manager;

import java.util.List;

public interface IManagerService {

    ManagerResponseDto createManager(CreateManagerRequestDto createManagerRequestDto);

    ManagerResponseDto updateManager(UpdateManagerRequestDto updateManagerRequestDto, Long id);

    ManagerResponseDto findManagerById(Long id);

    List<ManagerResponseDto> findAllManagers();

    void deleteManager(Long id);

    // Ayrılmış müdürü yeni satır açmadan geri alır; kişinin geçmişi tek kimlikte kalır.
    ManagerResponseDto reactivateManager(ReactivateEmployeeRequestDto reactivateEmployeeRequestDto, Long id);

    // Diğer servislerin (ör. BranchService) DTO'ya çevirmeden entity'ye
    // ulaşabilmesi için; IBranchService.getBranchEntityById ile aynı desen.
    Manager getManagerEntityById(Long id);
}
