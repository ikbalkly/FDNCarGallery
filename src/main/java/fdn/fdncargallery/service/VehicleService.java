package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.vehicle.UpdateVehicleRequestDto;
import fdn.fdncargallery.dto.vehicle.VehicleResponseDto;
import fdn.fdncargallery.entity.StockItem;
import fdn.fdncargallery.entity.Vehicle;
import fdn.fdncargallery.enums.CarStatus;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IVehicleMapper;
import fdn.fdncargallery.repository.IStockItemRepository;
import fdn.fdncargallery.repository.IVehicleRepository;
import fdn.fdncargallery.service.interfaces.IVehicleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService implements IVehicleService {

    private final IVehicleRepository vehicleRepository;
    private final IStockItemRepository stockItemRepository;
    private final IVehicleMapper vehicleMapper;
    private final SecurityService securityService;

    @Transactional
    @Override
    public VehicleResponseDto updateVehicle(UpdateVehicleRequestDto updateVehicleRequestDto, Long id) {

        Vehicle vehicle = getVehicleEntityById(id);

        checkVehicleAccess(id);

        vehicleMapper.updateVehicleFromDto(updateVehicleRequestDto, vehicle);

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        log.info("Araç künyesi güncellendi. vehicleId: {}, vin: {}", updatedVehicle.getId(), updatedVehicle.getVin());

        return vehicleMapper.toResponse(updatedVehicle);
    }

    @Transactional
    @Override
    public VehicleResponseDto findVehicleById(Long id) {

        Vehicle vehicle = getVehicleEntityById(id);

        checkVehicleAccess(id);

        return vehicleMapper.toResponse(vehicle);
    }

    @Override
    public Vehicle getVehicleEntityById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.VEHICLE_NOT_FOUND, id.toString())));
    }

    private void checkVehicleAccess(Long vehicleId) {

        Long holdingBranchId = stockItemRepository.findFirstByVehicleIdAndStatusNot(vehicleId, CarStatus.SOLD)
                .map(stockItem -> stockItem.getBranch().getId())
                .orElse(null);

        securityService.checkBranchAccess(holdingBranchId);
    }
}
