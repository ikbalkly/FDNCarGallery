package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("MANAGER")
@EqualsAndHashCode(callSuper = true)
public class Manager extends BaseEmployee {

    // müdürün kendi max indirim oranı
    @Column(precision = 5, scale = 2)
    private BigDecimal maxDiscountRate;

    // şubenin aylık satış hedefi
    @Column(precision = 15, scale = 2)
    private BigDecimal branchMonthlySalesTarget;

    // eğer şube hedefini tutturursa müdürün alacağı ekstra yönetim primi
    @Column(precision = 15, scale = 2)
    private BigDecimal managementBonus;
}
