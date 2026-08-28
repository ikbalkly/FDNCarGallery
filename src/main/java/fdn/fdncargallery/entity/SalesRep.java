package fdn.fdncargallery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sales_reps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SalesRep extends BaseEmployee {

    // satış temsilcisinin prim oranı -> 12.345 gibi veya 0.123
    @Column(nullable = false, precision = 5, scale = 3)
    private BigDecimal commissionRate;

    // aylık toplam satış adeti
    @Column(nullable = false)
    private Long monthlySalesCount = 0L;
}
