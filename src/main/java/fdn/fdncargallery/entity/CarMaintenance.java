package fdn.fdncargallery.entity;

import fdn.fdncargallery.enums.MaintenanceType;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "car_maintenances")
@SQLRestriction("deleted_at is null")
public class CarMaintenance extends BaseEntity {

    // bakıma giren araba
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private StockItem stockItem;

    // bakıma gönderen personel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BaseEmployee employee;

    // açıklama
    @Column(nullable = false)
    private String description;

    // aracın bakım ve ekspertiz tipi
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType maintenanceType;

    // başlangıç tarihi
    @Column(nullable = false)
    private LocalDate startDate;

    // beklenen bitiş tarihi
    @Column(nullable = false)
    private LocalDate expectedEndDate;

    // araç ustadan teslim alındığı gün; null ise bakım sürüyor
    private LocalDate completedAt;

    // ücreti
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal cost;
}
