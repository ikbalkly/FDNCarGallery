package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.BaseEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEmployeeRepository extends JpaRepository<BaseEmployee, Long> {
    boolean existsByIdentityNumber(String identityNumber);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<BaseEmployee> findByUsername(String username);

    @Query("""
            SELECT e FROM BaseEmployee e
            LEFT JOIN FETCH e.branch
            WHERE e.username = :username
            """)
    Optional<BaseEmployee> findByUsernameForAuthentication(@Param("username") String username);
}
