package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IModelRepository extends JpaRepository<Model, Long> {

    Optional<Model> findByBrandIdAndModelNameIgnoreCase(Long brandId, String modelName);

    boolean existsByBrandIdAndModelNameIgnoreCase(Long brandId, String modelName);

    List<Model> findAllByBrandIdOrderByModelNameAsc(Long brandId);

    List<Model> findAllByOrderByModelNameAsc();
}
