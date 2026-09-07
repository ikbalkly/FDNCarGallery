package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByBrandNameIgnoreCase(String brandName);

    boolean existsByBrandNameIgnoreCase(String brandName);

    List<Brand> findAllByOrderByBrandNameAsc();
}
