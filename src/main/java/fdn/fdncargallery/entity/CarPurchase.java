package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "car_purchases")
public class CarPurchase extends BaseEntity {

   // alınan arabanın kalem kaydı
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, unique = true)
    private StockItem stockItem;

    // aracın alış tarihi
    private LocalDate purchaseDate;

    // aracı satan kişi müşteri
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Customer sellerCustomer;

    // alımı yapan çalışan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BaseEmployee employee;

    // galerinin müşteriye ödediği alış fiyatı
    @Column(nullable = false, precision = 15, scale = 2)
    @Digits(integer = 13, fraction = 2, message = "Alış fiyatı en fazla 2 ondalık basamak içerebilir")
    private BigDecimal purchasePrice;

    // açıklama yapılmak istenirse
    @Column(length = 500)
    private String purchaseNotes;
}
