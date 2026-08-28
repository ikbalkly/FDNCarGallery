package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.IVehicleController;
import fdn.fdncargallery.dto.vehicle.UpdateVehicleRequestDto;
import fdn.fdncargallery.dto.vehicle.VehicleResponseDto;
import fdn.fdncargallery.service.interfaces.IVehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'BRANCH_ADMIN', 'MANAGER')")
public class VehicleController implements IVehicleController {

    private final IVehicleService vehicleService;

    @PutMapping("/update_vehicle/{id}")
    public ResponseEntity<VehicleResponseDto> updateVehicle(@Valid @RequestBody UpdateVehicleRequestDto updateVehicleRequestDto,
                                                            @PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.updateVehicle(updateVehicleRequestDto, id));
    }

    @GetMapping("/list_vehicle/{id}")
    public ResponseEntity<VehicleResponseDto> findVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.findVehicleById(id));
    }
}
