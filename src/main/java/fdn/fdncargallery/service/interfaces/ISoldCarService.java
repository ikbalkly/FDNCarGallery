package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.soldCar.CreateSoldCarRequestDto;
import fdn.fdncargallery.dto.soldCar.MonthlySalesDto;
import fdn.fdncargallery.dto.soldCar.SoldCarResponseDto;

import java.util.List;

public interface ISoldCarService {

    SoldCarResponseDto createSoldCar(CreateSoldCarRequestDto requestDto);
    SoldCarResponseDto findSoldCarById(Long id);
    List<SoldCarResponseDto> findAllSoldCars();
    List<MonthlySalesDto> findMonthlySalesByEmployee(Long employeeId);
    void deleteSoldCar(Long id);
}
