package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.MonthlySalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMonthlySalesSummaryRepository extends JpaRepository<MonthlySalesSummary, Long> {

    boolean existsBySalesYearAndSalesMonth(int salesYear, int salesMonth);

    List<MonthlySalesSummary> findAllByEmployeeId(Long employeeId);

    List<MonthlySalesSummary> findAllByBranchIdAndSalesYearAndSalesMonth(Long branchId, int salesYear, int salesMonth);
}