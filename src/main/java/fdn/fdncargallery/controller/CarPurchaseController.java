package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.ICarPurchaseController;
import fdn.fdncargallery.dto.carPurchase.CarPurchaseResponseDto;
import fdn.fdncargallery.dto.carPurchase.CreateCarPurchaseRequestDto;
import fdn.fdncargallery.dto.carPurchase.UpdateCarPurchaseRequestDto;
import fdn.fdncargallery.service.interfaces.ICarPurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car-purchases")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','BRANCH_ADMIN','MANAGER','SALES_REP')")
public class CarPurchaseController implements ICarPurchaseController {

    private final ICarPurchaseService carPurchaseService;

    @PostMapping("/create_car_purchase")
    public ResponseEntity<CarPurchaseResponseDto> createCarPurchase(@Valid @RequestBody CreateCarPurchaseRequestDto request) {
        CarPurchaseResponseDto response = carPurchaseService.createCarPurchase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update_car_purchase/{id}")
    public ResponseEntity<CarPurchaseResponseDto> updateCarPurchase(@Valid @RequestBody UpdateCarPurchaseRequestDto request,
                                                                     @PathVariable Long id) {
        return ResponseEntity.ok(carPurchaseService.updateCarPurchase(request, id));
    }

    @GetMapping("/list_car_purchase/{id}")
    public ResponseEntity<CarPurchaseResponseDto> findCarPurchaseById(@PathVariable Long id) {
        return ResponseEntity.ok(carPurchaseService.findCarPurchaseById(id));
    }

    @GetMapping("/list_car_purchase")
    public ResponseEntity<List<CarPurchaseResponseDto>> findAllCarPurchases() {
        return ResponseEntity.ok(carPurchaseService.findAllCarPurchases());
    }
}
