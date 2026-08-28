package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IVehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVin(String name);
}
