package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.vehicle.UpdateVehicleRequestDto;
import fdn.fdncargallery.dto.vehicle.VehicleResponseDto;
import org.springframework.http.ResponseEntity;

public interface IVehicleController {

    public ResponseEntity<VehicleResponseDto> updateVehicle(UpdateVehicleRequestDto updateVehicleRequestDto, Long id);

    public ResponseEntity<VehicleResponseDto> findVehicleById(Long id);
}
