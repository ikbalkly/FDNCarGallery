package fdn.fdncargallery.dto.soldCar;

import fdn.fdncargallery.enums.Role;

import java.math.BigDecimal;

public record MonthlySalesTotals(
        Long employeeId,
        Role employeeRole,
        Long branchId,
        Integer monthlySalesTarget,
        BigDecimal targetBonusPerCar,
        Long saleCount,
        BigDecimal totalSales,
        BigDecimal totalCommission) {
}
