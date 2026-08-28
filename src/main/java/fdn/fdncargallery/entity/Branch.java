package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "branches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Branch extends BaseEntity {

    // şube adı
    @Column(nullable = false, unique = true)
    private String branchName;

    // bir adreste bir şube olabilir
    @OneToOne(cascade = CascadeType.ALL)
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
