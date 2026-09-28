package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.reservation.CancelReservationRequestDto;
import fdn.fdncargallery.dto.reservation.CreateReservationRequestDto;
import fdn.fdncargallery.dto.reservation.ReservationResponseDto;
import fdn.fdncargallery.dto.reservation.UpdateReservationRequestDto;

import java.util.List;

public interface IReservationService {

    ReservationResponseDto createReservation(CreateReservationRequestDto requestDto);
    ReservationResponseDto updateReservation(UpdateReservationRequestDto requestDto, Long id);
    ReservationResponseDto cancelReservation(CancelReservationRequestDto requestDto, Long id);
    ReservationResponseDto findReservationById(Long id);
    List<ReservationResponseDto> findAllReservations();
}
