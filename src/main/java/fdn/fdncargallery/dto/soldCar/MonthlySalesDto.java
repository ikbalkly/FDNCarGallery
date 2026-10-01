package fdn.fdncargallery.dto.soldCar;

import java.math.BigDecimal;

public record MonthlySalesDto(
        Integer year,
        Integer month,
        Long saleCount,
        BigDecimal totalSales,
        BigDecimal totalCommission) {
}
