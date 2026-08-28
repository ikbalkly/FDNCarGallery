package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.StockItem;
import fdn.fdncargallery.enums.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IStockItemRepository extends JpaRepository<StockItem, Long> {

    boolean existsByVehicleIdAndStatusNot(Long vehicleId, CarStatus status);

    boolean existsByPlateNumberAndStatusNot(String plateNumber, CarStatus status);

    List<StockItem> findAllByBranchId(Long branchId);

    Optional<StockItem> findFirstByVehicleIdAndStatusNot(Long vehicleId, CarStatus status);
}
