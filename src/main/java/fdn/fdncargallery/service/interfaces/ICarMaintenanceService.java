package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.carMaintenance.CarMaintenanceResponseDto;
import fdn.fdncargallery.dto.carMaintenance.CompleteCarMaintenanceRequestDto;
import fdn.fdncargallery.dto.carMaintenance.CreateCarMaintenanceRequestDto;
import fdn.fdncargallery.dto.carMaintenance.UpdateCarMaintenanceRequestDto;

import java.util.List;

public interface ICarMaintenanceService {

    CarMaintenanceResponseDto createCarMaintenance(CreateCarMaintenanceRequestDto requestDto);
    CarMaintenanceResponseDto updateCarMaintenance(UpdateCarMaintenanceRequestDto requestDto, Long id);
    CarMaintenanceResponseDto completeCarMaintenance(CompleteCarMaintenanceRequestDto requestDto, Long id);
    CarMaintenanceResponseDto findCarMaintenanceById(Long id);
    List<CarMaintenanceResponseDto> findAllCarMaintenances();
    void deleteCarMaintenance(Long id);
}
