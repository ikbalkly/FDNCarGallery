package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.reservation.CancelReservationRequestDto;
import fdn.fdncargallery.dto.reservation.CreateReservationRequestDto;
import fdn.fdncargallery.dto.reservation.ReservationResponseDto;
import fdn.fdncargallery.dto.reservation.UpdateReservationRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IReservationController {

    ResponseEntity<ReservationResponseDto> createReservation(CreateReservationRequestDto request);

    ResponseEntity<ReservationResponseDto> updateReservation(UpdateReservationRequestDto request, Long id);

    ResponseEntity<ReservationResponseDto> cancelReservation(CancelReservationRequestDto request, Long id);

    ResponseEntity<ReservationResponseDto> findReservationById(Long id);

    ResponseEntity<List<ReservationResponseDto>> findAllReservations();
}
