package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.employee.ReactivateEmployeeRequestDto;
import fdn.fdncargallery.dto.manager.CreateManagerRequestDto;
import fdn.fdncargallery.dto.manager.ManagerResponseDto;
import fdn.fdncargallery.dto.manager.UpdateManagerRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IManagerController {

    public ResponseEntity<ManagerResponseDto> createManager(CreateManagerRequestDto createManagerRequestDto);

    public ResponseEntity<ManagerResponseDto> updateManager(UpdateManagerRequestDto updateManagerRequestDto, Long id);

    public ResponseEntity<ManagerResponseDto> findManagerById(Long id);

    public ResponseEntity<List<ManagerResponseDto>> findAllManagers();

    public ResponseEntity<Void> deleteManager(Long id);

    public ResponseEntity<ManagerResponseDto> reactivateManager(ReactivateEmployeeRequestDto reactivateEmployeeRequestDto, Long id);
}
