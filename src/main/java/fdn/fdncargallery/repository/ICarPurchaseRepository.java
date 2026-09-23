package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.CarPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICarPurchaseRepository extends JpaRepository<CarPurchase, Long> {

    List<CarPurchase> findAllByOrderByPurchaseDateDesc();

    List<CarPurchase> findAllByStockItem_Branch_IdOrderByPurchaseDateDesc(long branchId);

    List<CarPurchase> findAllByEmployeeIdOrderByPurchaseDateDesc(Long employeeId);
}
