package fdn.fdncargallery.service.interfaces;

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

    // Diğer servislerin (ör. BranchService) DTO'ya çevirmeden entity'ye
    // ulaşabilmesi için; IBranchService.getBranchEntityById ile aynı desen.
    Manager getManagerEntityById(Long id);
}
