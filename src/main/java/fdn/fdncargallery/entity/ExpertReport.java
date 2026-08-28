package fdn.fdncargallery.entity;

import jakarta.persistence.*;
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
@Table(name = "expert_reports")
public class ExpertReport extends BaseEntity {

    // expertize gönderilen aracın kalem kaydı
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private StockItem stockItem;

    // şirket adı
    @Column(nullable = false)
    private String reportCompany;

    // rapor tarihi
    @Column(nullable = false)
    private LocalDate reportDate;

    // motor performans yüzdesi
    private String enginePerformancePercentage; // Örn: "%85"

    // aracın hasar kaydı
    private boolean hasHeavyDamageRecord; // Ağır hasar kaydı (Tramer) var mı?

    @Column(precision = 15, scale = 2)
    private BigDecimal tramerTotalAmount; // Toplam Tramer Hasar Tutarı

    @Column(length = 1000)
    private String expertDetails; // Kaporta boya değişen parça detayları

}
