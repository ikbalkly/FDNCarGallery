package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.carPurchase.CarPurchaseResponseDto;
import fdn.fdncargallery.dto.carPurchase.CreateCarPurchaseRequestDto;
import fdn.fdncargallery.dto.carPurchase.UpdateCarPurchaseRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICarPurchaseController {

    ResponseEntity<CarPurchaseResponseDto> createCarPurchase(CreateCarPurchaseRequestDto request);

    ResponseEntity<CarPurchaseResponseDto> updateCarPurchase(UpdateCarPurchaseRequestDto request, Long id);

    ResponseEntity<CarPurchaseResponseDto> findCarPurchaseById(Long id);

    ResponseEntity<List<CarPurchaseResponseDto>> findAllCarPurchases();
}
