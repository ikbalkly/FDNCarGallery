package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

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

    @Override
    public BigDecimal commissionRateForSale() {
        return commissionRate != null ? commissionRate : BigDecimal.ZERO;
    }

    @Override
    public void recordSale() {
        monthlySalesCount = (monthlySalesCount == null ? 0L : monthlySalesCount) + 1;
    }

    // geçen ayın satışı iptal edilirse sayaç ay başında zaten sıfırlanmıştır
    @Override
    public void revertSale(LocalDateTime saleDate) {
        if (monthlySalesCount != null && monthlySalesCount > 0 && YearMonth.from(saleDate).equals(YearMonth.now())) {
            monthlySalesCount--;
        }
    }
}
