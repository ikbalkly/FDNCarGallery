package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("SALES_REP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SalesRep extends BaseEmployee {

    // satış temsilcisinin prim oranı -> 12.345 gibi veya 0.123
    @Column(nullable = true, precision = 5, scale = 3)
    private BigDecimal commissionRate;

    // aylık toplam satış adeti
    @Column(nullable = true)
    private Long monthlySalesCount = 0L;
}
