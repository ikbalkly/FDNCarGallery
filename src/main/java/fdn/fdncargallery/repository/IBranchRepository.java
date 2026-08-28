package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IBranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByAddressId(Long addressId);

    Optional<Branch> findByManagerId(Long managerId);

    boolean existsByBranchName(String branchName);
}
