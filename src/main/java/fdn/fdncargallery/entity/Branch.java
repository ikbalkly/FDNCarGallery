package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "branches")
@SQLRestriction("deleted_at is null")
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
}
