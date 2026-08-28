package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.SystemAdmin;
import fdn.fdncargallery.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISystemAdminRepository extends JpaRepository<SystemAdmin, Long> {

    @Query("""
            SELECT sa FROM SystemAdmin sa
            WHERE sa.active = true
              AND sa.userAccount.role = :role
            """)
    List<SystemAdmin> findAllActiveByRole(@Param("role") Role role);

    @Query("""
            SELECT sa FROM SystemAdmin sa
            WHERE sa.active = true
              AND sa.branch.id = :branchId
              AND sa.userAccount.role = :role
            """)
    List<SystemAdmin> findAllActiveByRoleAndBranch(@Param("role") Role role, @Param("branchId") Long branchId);

    @Query("""
            SELECT COUNT(sa) > 0 FROM SystemAdmin sa
            WHERE sa.active = true
              AND sa.branch.id = :branchId
              AND sa.userAccount.role = :role
            """)
    boolean existsActiveByRoleAndBranch(@Param("role") Role role, @Param("branchId") Long branchId);
}
