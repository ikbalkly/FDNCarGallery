package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.soldCar.BranchMonthlySalesDto;
import fdn.fdncargallery.dto.soldCar.CreateSoldCarRequestDto;
import fdn.fdncargallery.dto.soldCar.MonthlySalesDto;
import fdn.fdncargallery.dto.soldCar.SoldCarResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ISoldCarController {

    ResponseEntity<SoldCarResponseDto> createSoldCar(CreateSoldCarRequestDto request);

    ResponseEntity<SoldCarResponseDto> findSoldCarById(Long id);

    ResponseEntity<List<SoldCarResponseDto>> findAllSoldCars();

    ResponseEntity<List<MonthlySalesDto>> findMonthlySalesByEmployee(Long employeeId);

    ResponseEntity<List<BranchMonthlySalesDto>> findMonthlySalesByBranch(Long branchId, int year, int month);

    ResponseEntity<Void> deleteSoldCar(Long id);
}
