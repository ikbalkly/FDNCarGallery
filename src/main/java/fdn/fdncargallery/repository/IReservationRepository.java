package fdn.fdncargallery.repository;

import fdn.fdncargallery.entity.Reservation;
import fdn.fdncargallery.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByOrderByReservationDateDesc();

    List<Reservation> findAllByStockItem_Branch_IdOrderByReservationDateDesc(Long branchId);

    List<Reservation> findAllByEmployeeIdOrderByReservationDateDesc(Long employeeId);

    List<Reservation> findAllByStatusAndExpirationDateBefore(ReservationStatus status, LocalDateTime dateTime);

    Optional<Reservation> findByStockItemIdAndStatus(Long stockItemId, ReservationStatus status);
}
