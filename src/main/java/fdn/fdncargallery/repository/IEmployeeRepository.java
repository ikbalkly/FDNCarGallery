package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.BaseEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEmployeeRepository extends JpaRepository<BaseEmployee, Long> {
    boolean existsByIdentityNumber(String identityNumber);
}
