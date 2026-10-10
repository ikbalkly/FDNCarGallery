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

    // aylık toplam satış adeti
    @Column(nullable = true)
    private Long monthlySalesCount = 0L;

    // komisyon oranı aracın satıldığı şubeden gelir; şubede oran girilmemişse komisyon yok
    @Override
    public BigDecimal commissionRateForSale(Branch branch) {
        return branch.getCommissionRate() != null ? branch.getCommissionRate() : BigDecimal.ZERO;
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
