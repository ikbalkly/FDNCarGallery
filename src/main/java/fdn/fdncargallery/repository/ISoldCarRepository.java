package fdn.fdncargallery.repository;

import fdn.fdncargallery.dto.soldCar.MonthlySalesDto;
import fdn.fdncargallery.entity.SoldCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISoldCarRepository extends JpaRepository<SoldCar, Long> {

    List<SoldCar> findAllByOrderBySaleDateDesc();

    List<SoldCar> findAllByStockItem_Branch_IdOrderBySaleDateDesc(Long branchId);

    List<SoldCar> findAllByEmployeeIdOrderBySaleDateDesc(Long employeeId);

    @Query("""
            SELECT new fdn.fdncargallery.dto.soldCar.MonthlySalesDto(
                    YEAR(s.saleDate), MONTH(s.saleDate), COUNT(s), SUM(s.salePrice), ROUND(SUM(s.salePrice * s.commissionRate) / 100, 2))
            FROM SoldCar s
            WHERE s.employee.id = :employeeId
            GROUP BY YEAR(s.saleDate), MONTH(s.saleDate)
            ORDER BY YEAR(s.saleDate) DESC, MONTH(s.saleDate) DESC
            """)
    List<MonthlySalesDto> findMonthlySalesByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("""
            SELECT new fdn.fdncargallery.dto.soldCar.MonthlySalesDto(
                    YEAR(s.saleDate), MONTH(s.saleDate), COUNT(s), SUM(s.salePrice), ROUND(SUM(s.salePrice * s.commissionRate) / 100, 2))
            FROM SoldCar s
            WHERE s.employee.id = :employeeId AND s.stockItem.branch.id = :branchId
            GROUP BY YEAR(s.saleDate), MONTH(s.saleDate)
            ORDER BY YEAR(s.saleDate) DESC, MONTH(s.saleDate) DESC
            """)
    List<MonthlySalesDto> findMonthlySalesByEmployeeIdAndBranchId(@Param("employeeId") Long employeeId,
                                                                  @Param("branchId") Long branchId);
}
