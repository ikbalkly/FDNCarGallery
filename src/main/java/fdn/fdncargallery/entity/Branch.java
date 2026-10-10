package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "branches")
// @SQLRestriction YOK: filtre ilişki yüklenirken de uygulanıyor ve kapatılmış şubeye
// bağlı personel/stok kaydı açılamaz hale geliyordu. Görünürlük sorgularda yönetilir.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Branch extends BaseEntity {

    // şube adı
    @Column(nullable = false, unique = true)
    private String branchName;

    // adres -> şube satırına gömülür, ayrı tablo yok
    @Embedded
    private Address address;

    // bir şubede 1 müdür olabilir
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = true)
    private Manager manager;

    // şubedeki tüm araçların listesi
    @OneToMany(mappedBy = "branch")
    private List<StockItem> stockItems;

    // şubede çalışan tüm personellerin listesi
    @OneToMany(mappedBy = "branch")
    private List<BaseEmployee> employees;

    // satış temsilcilerinin komisyon oranı (yüzde, 0-1 arası) -> 0.200 = %0,2
    @Column(name = "commission_rate", precision = 5, scale = 3)
    private BigDecimal commissionRate;

    // temsilci başına aylık satış hedefi (araç adedi)
    @Column(name = "monthly_sales_target")
    private Integer monthlySalesTarget;

    // hedefi aşan her araç için temsilciye verilen prim
    @Column(name = "target_bonus_per_car", precision = 15, scale = 2)
    private BigDecimal targetBonusPerCar;
}
