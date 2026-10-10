package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.ISoldCarController;
import fdn.fdncargallery.dto.soldCar.BranchMonthlySalesDto;
import fdn.fdncargallery.dto.soldCar.CreateSoldCarRequestDto;
import fdn.fdncargallery.dto.soldCar.MonthlySalesDto;
import fdn.fdncargallery.dto.soldCar.SoldCarResponseDto;
import fdn.fdncargallery.service.interfaces.ISoldCarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sold-cars")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','BRANCH_ADMIN','MANAGER','SALES_REP')")
public class SoldCarController implements ISoldCarController {

    private final ISoldCarService soldCarService;

    @PostMapping("/create_sold_car")
    public ResponseEntity<SoldCarResponseDto> createSoldCar(@Valid @RequestBody CreateSoldCarRequestDto request) {
        SoldCarResponseDto response = soldCarService.createSoldCar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/list_sold_car/{id}")
    public ResponseEntity<SoldCarResponseDto> findSoldCarById(@PathVariable Long id) {
        return ResponseEntity.ok(soldCarService.findSoldCarById(id));
    }

    @GetMapping("/list_sold_car")
    public ResponseEntity<List<SoldCarResponseDto>> findAllSoldCars() {
        return ResponseEntity.ok(soldCarService.findAllSoldCars());
    }

    @GetMapping("/monthly_sales/{employeeId}")
    public ResponseEntity<List<MonthlySalesDto>> findMonthlySalesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(soldCarService.findMonthlySalesByEmployee(employeeId));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','BRANCH_ADMIN','MANAGER')")
    @GetMapping("/branch_monthly_sales/{branchId}/{year}/{month}")
    public ResponseEntity<List<BranchMonthlySalesDto>> findMonthlySalesByBranch(@PathVariable Long branchId,
                                                                                @PathVariable int year,
                                                                                @PathVariable int month) {
        return ResponseEntity.ok(soldCarService.findMonthlySalesByBranch(branchId, year, month));
    }

    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN','BRANCH_ADMIN','MANAGER')")
    @DeleteMapping("/delete_sold_car/{id}")
    public ResponseEntity<Void> deleteSoldCar(@PathVariable Long id) {
        soldCarService.deleteSoldCar(id);
        return ResponseEntity.noContent().build();
    }
}
