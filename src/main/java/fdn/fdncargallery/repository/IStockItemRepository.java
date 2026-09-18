package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.StockItem;
import fdn.fdncargallery.enums.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IStockItemRepository extends JpaRepository<StockItem, Long> {

    boolean existsByVehicleIdAndStatusNotAndDeletedAtIsNull(Long vehicleId, CarStatus status);

    boolean existsByPlateNumberAndStatusNotAndDeletedAtIsNull(String plateNumber, CarStatus status);

    List<StockItem> findAllByDeletedAtIsNull();

    List<StockItem> findAllByBranchIdAndDeletedAtIsNull(Long branchId);

    Optional<StockItem> findFirstByVehicleIdAndStatusNotAndDeletedAtIsNull(Long vehicleId, CarStatus status);
}