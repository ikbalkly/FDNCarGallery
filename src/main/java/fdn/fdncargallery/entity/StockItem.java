package fdn.fdncargallery.entity;

import fdn.fdncargallery.enums.CarCondition;
import fdn.fdncargallery.enums.CarStatus;
import fdn.fdncargallery.enums.WarrantyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockItem extends BaseEntity {

    // Aynı araç yıllar sonra geri gelirse ikinci bir StockItem açılır.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Vehicle vehicle;

    // şube bilgisi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Branch branch;

    // unique DEĞİL: aynı plaka farklı dönemlerde farklı stok kalemlerinde görünebilir.
    @Column(nullable = false, length = 20)
    private String plateNumber;

    // kilometre
    @Column(nullable = false)
    private Integer mileage;

    // renk
    @Column(nullable = false)
    private String color;

    // liste fiyatı
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal listPrice;

    // aracın  durumu
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    // sıfır mı 2. el mi?
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarCondition condition;

    // garanti tipi
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarrantyType warrantyType;

    @Column(nullable = false)
    private LocalDate acquiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BaseEmployee employee;

    // satıldığı tarih. Kalem AVAILABLE olarak açılırken null'dır;
    // satış akışı tamamlanınca dolar.
    private LocalDate soldAt;

    // aynı anda aynı aracı satılmaması için
    @Version
    private Long version;
}