package fdn.fdncargallery.dto.soldCar;

import fdn.fdncargallery.enums.Role;

import java.math.BigDecimal;

public record BranchMonthlySalesDto(
        Long employeeId,
        String employeeFullName,
        Role role,
        Long saleCount,
        BigDecimal totalSales,
        BigDecimal totalCommission,
        BigDecimal targetBonus) {
}
