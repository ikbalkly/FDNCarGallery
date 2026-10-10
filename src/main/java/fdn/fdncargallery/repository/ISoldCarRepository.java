package fdn.fdncargallery.repository;

import fdn.fdncargallery.dto.soldCar.MonthlySalesTotals;
import fdn.fdncargallery.entity.SoldCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ISoldCarRepository extends JpaRepository<SoldCar, Long> {

    List<SoldCar> findAllByOrderBySaleDateDesc();

    List<SoldCar> findAllByStockItem_Branch_IdOrderBySaleDateDesc(Long branchId);

    List<SoldCar> findAllByEmployeeIdOrderBySaleDateDesc(Long employeeId);

    // bir aydaki satışlar personel + satışın yapıldığı şube bazında toplanır; şubenin hedefi ve tutarı da okunur
    @Query("""
            SELECT new fdn.fdncargallery.dto.soldCar.MonthlySalesTotals(
                    e.id, e.role, b.id, b.monthlySalesTarget, b.targetBonusPerCar,
                    COUNT(s), SUM(s.salePrice), ROUND(SUM(s.salePrice * s.commissionRate) / 100, 2))
            FROM SoldCar s
            JOIN s.employee e
            JOIN s.stockItem si
            JOIN si.branch b
            WHERE s.saleDate >= :start AND s.saleDate < :end
            GROUP BY e.id, e.role, b.id, b.monthlySalesTarget, b.targetBonusPerCar
            """)
    List<MonthlySalesTotals> findMonthlyTotals(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    Optional<SoldCar> findFirstByOrderBySaleDateAsc();
}
