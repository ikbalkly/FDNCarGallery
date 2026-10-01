package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sold_cars")
@SQLRestriction("deleted_at is null")

public class SoldCar extends BaseEntity {

    //satışı yapan personel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BaseEmployee employee;

    // satılan aracın bilgisi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private StockItem stockItem;

    // Satış anındaki prim oranı DONDURULUR.
    @Column(nullable = false, precision = 5, scale = 3)
    private BigDecimal commissionRate;

    // aracı alan müşteri
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Customer customer;

    // satışın tarih ve saat bilgileri
    @Column(nullable = false)
    private LocalDateTime saleDate;

    // aracın satış tutarı (indirim yapılmış olabilir)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal salePrice;
}
