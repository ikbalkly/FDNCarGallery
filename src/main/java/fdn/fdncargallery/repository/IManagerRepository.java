package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IManagerRepository extends JpaRepository<Manager, Long> {
    List<Manager> findAllByActiveTrue();

    List<Manager> findAllByBranchIdAndActiveTrue(Long branchId);
}