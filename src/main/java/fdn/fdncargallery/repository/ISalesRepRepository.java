package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.Manager;
import fdn.fdncargallery.entity.SalesRep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISalesRepRepository extends JpaRepository<SalesRep, Long> {

    List<SalesRep> findAllByActiveTrue();
    List<SalesRep> findAllByBranchIdAndActiveTrue(Long branchId);
}
