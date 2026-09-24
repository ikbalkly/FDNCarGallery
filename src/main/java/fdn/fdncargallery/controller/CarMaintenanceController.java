package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.ICarMaintenanceController;
import fdn.fdncargallery.dto.carMaintenance.CarMaintenanceResponseDto;
import fdn.fdncargallery.dto.carMaintenance.CompleteCarMaintenanceRequestDto;
import fdn.fdncargallery.dto.carMaintenance.CreateCarMaintenanceRequestDto;
import fdn.fdncargallery.dto.carMaintenance.UpdateCarMaintenanceRequestDto;
import fdn.fdncargallery.service.interfaces.ICarMaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car-maintenances")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','BRANCH_ADMIN','MANAGER','SALES_REP')")
public class CarMaintenanceController implements ICarMaintenanceController {

    private final ICarMaintenanceService carMaintenanceService;

    @PostMapping("/create_car_maintenance")
    public ResponseEntity<CarMaintenanceResponseDto> createCarMaintenance(@Valid @RequestBody CreateCarMaintenanceRequestDto request) {
        CarMaintenanceResponseDto response = carMaintenanceService.createCarMaintenance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update_car_maintenance/{id}")
    public ResponseEntity<CarMaintenanceResponseDto> updateCarMaintenance(@Valid @RequestBody UpdateCarMaintenanceRequestDto request,
                                                                           @PathVariable Long id) {
        return ResponseEntity.ok(carMaintenanceService.updateCarMaintenance(request, id));
    }

    @PutMapping("/complete_car_maintenance/{id}")
    public ResponseEntity<CarMaintenanceResponseDto> completeCarMaintenance(@Valid @RequestBody(required = false) CompleteCarMaintenanceRequestDto request,
                                                                             @PathVariable Long id) {
        return ResponseEntity.ok(carMaintenanceService.completeCarMaintenance(request, id));
    }

    @GetMapping("/list_car_maintenance/{id}")
    public ResponseEntity<CarMaintenanceResponseDto> findCarMaintenanceById(@PathVariable Long id) {
        return ResponseEntity.ok(carMaintenanceService.findCarMaintenanceById(id));
    }

    @GetMapping("/list_car_maintenance")
    public ResponseEntity<List<CarMaintenanceResponseDto>> findAllCarMaintenances() {
        return ResponseEntity.ok(carMaintenanceService.findAllCarMaintenances());
    }

    @DeleteMapping("/delete_car_maintenance/{id}")
    public ResponseEntity<Void> deleteCarMaintenance(@PathVariable Long id) {
        carMaintenanceService.deleteCarMaintenance(id);
        return ResponseEntity.noContent().build();
    }
}
