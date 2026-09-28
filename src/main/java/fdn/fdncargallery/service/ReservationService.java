package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.reservation.CancelReservationRequestDto;
import fdn.fdncargallery.dto.reservation.CreateReservationRequestDto;
import fdn.fdncargallery.dto.reservation.ReservationResponseDto;
import fdn.fdncargallery.dto.reservation.UpdateReservationRequestDto;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.Reservation;
import fdn.fdncargallery.entity.StockItem;
import fdn.fdncargallery.enums.CarStatus;
import fdn.fdncargallery.enums.ReservationStatus;
import fdn.fdncargallery.enums.Role;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IReservationMapper;
import fdn.fdncargallery.repository.IReservationRepository;
import fdn.fdncargallery.service.interfaces.ICustomerService;
import fdn.fdncargallery.service.interfaces.IReservationService;
import fdn.fdncargallery.service.interfaces.IStockItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService implements IReservationService {

    private static final int MAX_RESERVATION_DAYS = 30;

    private final IReservationRepository reservationRepository;
    private final IReservationMapper reservationMapper;
    private final IStockItemService stockItemService;
    private final ICustomerService customerService;
    private final SecurityService securityService;

    @Transactional
    @Override
    public ReservationResponseDto createReservation(CreateReservationRequestDto requestDto) {

        StockItem stockItem = stockItemService.getStockItemEntityById(requestDto.getStockItemId());
        securityService.checkBranchAccess(stockItem.getBranch().getId());

        // süresi geçmiş ama gece job bekleyen rezervasyon aracı kilitli tutmasın
        if (stockItem.getStatus() == CarStatus.RESERVED) {
            reservationRepository.findByStockItemIdAndStatus(stockItem.getId(), ReservationStatus.ACTIVE)
                    .filter(Reservation::isExpired)
                    .ifPresent(overdue -> {
                        expire(overdue);
                        // araç RESERVED → AVAILABLE → RESERVED döndüğü için dirty checking UPDATE atmaz, @Version devreye girmez;
                        // ara durum burada yazılır, aynı araca eşzamanlı ikinci rezervasyon 409 alır
                        reservationRepository.flush();
                    });
        }

        if (stockItem.getStatus() == CarStatus.RESERVED) {
            throw new BaseException(new ErrorMessage(MessageType.STOCK_ITEM_ALREADY_RESERVED, stockItem.getId().toString()));
        }

        // satılmış, bakımdaki ya da ekspertizdeki araç rezerve edilemez
        if (stockItem.getStatus() != CarStatus.AVAILABLE) {
            throw new BaseException(new ErrorMessage(MessageType.STOCK_ITEM_NOT_AVAILABLE_FOR_SALE,
                    "Yalnızca satışta (AVAILABLE) olan bir araç rezerve edilebilir. Mevcut durum: " + stockItem.getStatus()));
        }

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();

        Reservation reservation = reservationMapper.toEntity(requestDto);
        checkMaxDuration(reservation.getReservationDate(), reservation.getExpirationDate());

        reservation.setStockItem(stockItem);
        reservation.setCustomer(customerService.getCustomerEntityById(requestDto.getCustomerId()));
        reservation.setEmployee(currentEmployee);

        // StockItem'daki @Version aynı araca eşzamanlı iki rezervasyon açılmasını engeller
        stockItem.setStatus(CarStatus.RESERVED);

        Reservation saved = reservationRepository.saveAndFlush(reservation);

        log.info("Rezervasyon açıldı. id: {}, stockItemId: {}, müşteri id: {}, bitiş: {}, personel id: {}",
                saved.getId(), stockItem.getId(), saved.getCustomer().getId(), saved.getExpirationDate(), currentEmployee.getId());

        return reservationMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public ReservationResponseDto updateReservation(UpdateReservationRequestDto requestDto, Long id) {

        Reservation reservation = getReservationEntityById(id);
        checkReservationAccess(reservation);
        reservation.requireActive("güncellenemez");

        // süresi geçen rezervasyon gece görevini beklemeden bitmiş sayılır; uzatma yerine yeni rezervasyon açılır
        if (reservation.isExpired()) {
            throw new BaseException(new ErrorMessage(MessageType.RESERVATION_NOT_ACTIVE,
                    "Rezervasyonun süresi " + reservation.getExpirationDate() + " tarihinde doldu. Yeni rezervasyon açın."));
        }

        checkMaxDuration(reservation.getReservationDate(), requestDto.getExpirationDate());

        reservationMapper.updateReservationFromDto(requestDto, reservation);

        Reservation updated = reservationRepository.saveAndFlush(reservation);

        log.info("Rezervasyon güncellendi. id: {}, bitiş: {}, kapora: {}", id, updated.getExpirationDate(), updated.getDepositAmount());
        return reservationMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public ReservationResponseDto cancelReservation(CancelReservationRequestDto requestDto, Long id) {

        Reservation reservation = getReservationEntityById(id);
        checkReservationAccess(reservation);

        reservation.cancel(requestDto.getReason());
        releaseStockItem(reservation.getStockItem());

        Reservation cancelled = reservationRepository.saveAndFlush(reservation);

        log.info("Rezervasyon iptal edildi. id: {}, stockItemId: {}", id, cancelled.getStockItem().getId());
        return reservationMapper.toResponse(cancelled);
    }

    @Transactional
    @Override
    public ReservationResponseDto findReservationById(Long id) {

        Reservation reservation = getReservationEntityById(id);
        checkReservationAccess(reservation);

        return reservationMapper.toResponse(reservation);
    }

    @Transactional
    @Override
    public List<ReservationResponseDto> findAllReservations() {

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();

        List<Reservation> reservations;
        if (securityService.isSuperAdmin()) {
            reservations = reservationRepository.findAllByOrderByReservationDateDesc();
        } else if (currentEmployee.getRole() == Role.SALES_REP) {
            reservations = reservationRepository.findAllByEmployeeIdOrderByReservationDateDesc(currentEmployee.getId());
        } else {
            reservations = reservationRepository.findAllByStockItem_Branch_IdOrderByReservationDateDesc(securityService.getCurrentBranchId());
        }

        return reservations.stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    // her gece 03:00'te bitiş saati geçmiş aktif rezervasyonlar kapanır, araçlar satışa döner
    @Scheduled(cron = "0 0 3 * * *", zone = "Europe/Istanbul")
    @Transactional
    public void expireOverdueReservations() {
        List<Reservation> overdue = reservationRepository.findAllByStatusAndExpirationDateBefore(ReservationStatus.ACTIVE, LocalDateTime.now());
        overdue.forEach(this::expire);
        log.info("Süresi dolan rezervasyonlar kapatıldı. kapatılan: {}", overdue.size());
    }

    private void expire(Reservation reservation) {
        reservation.markExpired();
        releaseStockItem(reservation.getStockItem());
        log.info("Rezervasyonun süresi doldu. id: {}, stockItemId: {}",
                reservation.getId(), reservation.getStockItem().getId());
    }

    // araç arada başka bir akışla durum değiştirdiyse (ör. satıldıysa) ezilmesin
    private void releaseStockItem(StockItem stockItem) {
        if (stockItem.getStatus() != CarStatus.RESERVED) {
            log.warn("Rezervasyon kapandı ama araç RESERVED değil, durumu korunuyor. stockItemId: {}, durum: {}",
                    stockItem.getId(), stockItem.getStatus());
            return;
        }
        stockItem.setStatus(CarStatus.AVAILABLE);
    }

    private Reservation getReservationEntityById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.RESERVATION_NOT_FOUND, id.toString())));
    }

    // şube kontrolüne ek olarak: satış temsilcisi yalnızca kendi açtığı rezervasyona erişebilir
    private void checkReservationAccess(Reservation reservation) {

        securityService.checkBranchAccess(reservation.getStockItem().getBranch().getId());

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();
        if (currentEmployee.getRole() == Role.SALES_REP && !reservation.getEmployee().getId().equals(currentEmployee.getId())) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED,
                    "Sadece kendi açtığınız rezervasyonlara erişebilirsiniz."));
        }
    }

    // taban rezervasyon tarihi: uzatmalar dahil toplam tutma süresi 30 günü geçemez
    private void checkMaxDuration(LocalDateTime reservationDate, LocalDateTime expirationDate) {
        LocalDateTime latest = reservationDate.plusDays(MAX_RESERVATION_DAYS);
        if (expirationDate.isAfter(latest)) {
            throw new BaseException(new ErrorMessage(MessageType.VALIDATION_ERROR,
                    "Rezervasyon en fazla " + MAX_RESERVATION_DAYS + " gün sürebilir. Bitiş tarihi en geç " + latest.toLocalDate() + " olabilir."));
        }
    }
}
