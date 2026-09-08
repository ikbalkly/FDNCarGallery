package fdn.fdncargallery.entity;

import fdn.fdncargallery.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted_at is null")
public class Reservation extends BaseEntity {

    // rezerve edilen araç
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private StockItem stockItem;

    // rezerve yapan müşteri
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Customer customer;

    //rezervasyonu yapan employee
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BaseEmployee employee;

    // rezervasyon tarihi
    @Column(nullable = false)
    private LocalDateTime reservationDate = LocalDateTime.now();

    // rezervasyon son geçerlilik tarihi
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    // kapora
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal depositAmount = BigDecimal.ZERO;

    // rezervasyon durumu - enum
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    // not veya iptal gerekçesi
    @Column(length = 500)
    private String note;

     // Müşteri vazgeçti ya da temsilci iptal etti.
    public void cancel(String reason) {
        requireActive("iptal edilemez");
        this.status = ReservationStatus.CANCELLED;
        this.note = reason;
    }

     // Süresi doldu, araç tekrar satışa çıkacak.
    public void markExpired() {
        requireActive("süresi dolmuş sayılamaz");
        this.status = ReservationStatus.EXPIRED;
    }

     // Rezervasyon satışa dönüştü.
    public void markConverted() {
        requireActive("satışa çevrilemez");
        this.status = ReservationStatus.CONVERTED;
    }

    @Transient
    public boolean isActive() {
        return status == ReservationStatus.ACTIVE;
    }

    @Transient
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDateTime.now());
    }

    private void requireActive(String action) {
        if (status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Rezervasyon " + action + ", mevcut durumu: " + status
                            + " (id=" + getId() + ")");
        }
    }
}
