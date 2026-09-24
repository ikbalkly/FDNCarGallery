package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.carMaintenance.CarMaintenanceResponseDto;
import fdn.fdncargallery.dto.carMaintenance.CompleteCarMaintenanceRequestDto;
import fdn.fdncargallery.dto.carMaintenance.CreateCarMaintenanceRequestDto;
import fdn.fdncargallery.dto.carMaintenance.UpdateCarMaintenanceRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICarMaintenanceController {

    ResponseEntity<CarMaintenanceResponseDto> createCarMaintenance(CreateCarMaintenanceRequestDto request);

    ResponseEntity<CarMaintenanceResponseDto> updateCarMaintenance(UpdateCarMaintenanceRequestDto request, Long id);

    ResponseEntity<CarMaintenanceResponseDto> completeCarMaintenance(CompleteCarMaintenanceRequestDto request, Long id);

    ResponseEntity<CarMaintenanceResponseDto> findCarMaintenanceById(Long id);

    ResponseEntity<List<CarMaintenanceResponseDto>> findAllCarMaintenances();

    ResponseEntity<Void> deleteCarMaintenance(Long id);
}
