package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.CarMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICarMaintenanceRepository extends JpaRepository<CarMaintenance, Long> {

    List<CarMaintenance> findAllByOrderByStartDateDesc();

    List<CarMaintenance> findAllByStockItem_Branch_IdOrderByStartDateDesc(Long branchId);

    List<CarMaintenance> findAllByEmployeeIdOrderByStartDateDesc(Long employeeId);
}
