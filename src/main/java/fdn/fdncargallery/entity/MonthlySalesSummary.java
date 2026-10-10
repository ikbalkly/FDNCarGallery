package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "monthly_sales_summaries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "branch_id", "sales_year", "sales_month"}))
public class MonthlySalesSummary extends BaseEntity {

    // satışları yapan personel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BaseEmployee employee;

    // satışların yapıldığı şube
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Branch branch;

    // yıl
    @Column(name = "sales_year", nullable = false)
    private int salesYear;

    // ay
    @Column(name = "sales_month", nullable = false)
    private int salesMonth;

    // satılan araç adedi
    @Column(nullable = false)
    private long saleCount;

    // ciro
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalSales;

    // komisyon toplamı
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCommission;

    // kapanış anındaki şube hedefi
    @Column(name = "monthly_sales_target")
    private Integer monthlySalesTarget;

    // kapanış anındaki araç başı hedef primi
    @Column(name = "target_bonus_per_car", precision = 15, scale = 2)
    private BigDecimal targetBonusPerCar;

    // hedef primi
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal targetBonus;
}
