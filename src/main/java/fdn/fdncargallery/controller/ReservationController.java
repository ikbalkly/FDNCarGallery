package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.IReservationController;
import fdn.fdncargallery.dto.reservation.CancelReservationRequestDto;
import fdn.fdncargallery.dto.reservation.CreateReservationRequestDto;
import fdn.fdncargallery.dto.reservation.ReservationResponseDto;
import fdn.fdncargallery.dto.reservation.UpdateReservationRequestDto;
import fdn.fdncargallery.service.interfaces.IReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN','BRANCH_ADMIN','MANAGER','SALES_REP')")
public class ReservationController implements IReservationController {

    private final IReservationService reservationService;

    @PostMapping("/create_reservation")
    public ResponseEntity<ReservationResponseDto> createReservation(@Valid @RequestBody CreateReservationRequestDto request) {
        ReservationResponseDto response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update_reservation/{id}")
    public ResponseEntity<ReservationResponseDto> updateReservation(@Valid @RequestBody UpdateReservationRequestDto request,
                                                                    @PathVariable Long id) {
        return ResponseEntity.ok(reservationService.updateReservation(request, id));
    }

    @PutMapping("/cancel_reservation/{id}")
    public ResponseEntity<ReservationResponseDto> cancelReservation(@Valid @RequestBody CancelReservationRequestDto request,
                                                                    @PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(request, id));
    }

    @GetMapping("/list_reservation/{id}")
    public ResponseEntity<ReservationResponseDto> findReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findReservationById(id));
    }

    @GetMapping("/list_reservation")
    public ResponseEntity<List<ReservationResponseDto>> findAllReservations() {
        return ResponseEntity.ok(reservationService.findAllReservations());
    }
}
