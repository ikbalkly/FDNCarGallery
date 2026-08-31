package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.vehicle.UpdateVehicleRequestDto;
import fdn.fdncargallery.dto.vehicle.VehicleResponseDto;
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

    // vehicle repo'da gönderilen id ile ilgili kaydı bulup return ediyor
    @Override
    public Vehicle getVehicleEntityById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.VEHICLE_NOT_FOUND, id.toString())));
    }

    // vehicle'ın kullanıcı tarafından görüntülenip görüntülenemeyeceğini şube (branch) bazlı yetkiye göre kontrol ediyor.
    private void checkVehicleAccess(Long vehicleId) {

        // Verilen vehicleId'ye ait, durumu SOLD (satılmış) olmayan ilk stok kaydını arıyor. Yani "bu araç şu an hangi şubenin stoğunda duruyor?" sorusunun cevabını arıyor
        Long holdingBranchId = stockItemRepository.findFirstByVehicleIdAndStatusNot(vehicleId, CarStatus.SOLD)
                //Kayıt bulunduysa, o stok kaydının bağlı olduğu şubenin ID'sini alıyor. findFirst... bir Optional döndürdüğü için map ile içeriye giriliyor.
                .map(stockItem -> stockItem.getBranch().getId())
                .orElse(null);
        // Giriş yapmış kullanıcının o şubeye erişim yetkisi olup olmadığını kontrol ediyor
        securityService.checkBranchAccess(holdingBranchId);
    }
}
