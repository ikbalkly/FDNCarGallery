package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.vehicle.UpdateVehicleRequestDto;
import fdn.fdncargallery.dto.vehicle.VehicleResponseDto;
import fdn.fdncargallery.entity.Vehicle;

/**
 * Araç künyesi (Vehicle) yönetimi.
 * <p>
 * createVehicle YOKTUR ve bilinçli olarak eklenmemiştir: araç kütüğüne kayıt,
 * yalnızca stok girişi sırasında VIN üzerinden açılır (StockItemService).
 * Bağımsız bir "araç oluştur" ucu, hiçbir stok kaydına bağlı olmayan öksüz
 * Vehicle satırları üretirdi.
 */
public interface IVehicleService {

    VehicleResponseDto updateVehicle(UpdateVehicleRequestDto updateVehicleRequestDto, Long id);

    VehicleResponseDto findVehicleById(Long id);

    Vehicle getVehicleEntityById(Long id);
}
