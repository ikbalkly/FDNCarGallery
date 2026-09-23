package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.carPurchase.CarPurchaseResponseDto;
import fdn.fdncargallery.dto.carPurchase.CreateCarPurchaseRequestDto;
import fdn.fdncargallery.dto.carPurchase.UpdateCarPurchaseRequestDto;

import java.util.List;

public interface ICarPurchaseService {

    CarPurchaseResponseDto  createCarPurchase(CreateCarPurchaseRequestDto requestDto);
    CarPurchaseResponseDto updateCarPurchase(UpdateCarPurchaseRequestDto requestDto,Long id);
    CarPurchaseResponseDto findCarPurchaseById(Long id);
    List<CarPurchaseResponseDto> findAllCarPurchases();
}
