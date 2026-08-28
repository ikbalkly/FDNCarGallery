package fdn.fdncargallery.mapper;

import fdn.fdncargallery.dto.stockItem.CreateStockItemRequestDto;
import fdn.fdncargallery.dto.stockItem.StockItemResponseDto;
import fdn.fdncargallery.dto.stockItem.UpdateStockItemRequestDto;
import fdn.fdncargallery.entity.StockItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {IVehicleMapper.class}, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface IStockItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "acquiredAt", ignore = true)
    @Mapping(target = "soldAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "employee", ignore = true)
    StockItem toEntity(CreateStockItemRequestDto request);

    // vehicle alanı IVehicleMapper.toResponse ile otomatik çevrilir
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.branchName")
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeFullName", expression = "java(stockItem.getEmployee().getName() + \" \" + stockItem.getEmployee().getSurname())")
    StockItemResponseDto toResponse(StockItem stockItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "branch", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "acquiredAt", ignore = true)
    @Mapping(target = "soldAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    // Girişi yapan personel sonradan değişmez: kim aldıysa o kalır.
    @Mapping(target = "employee", ignore = true)
    void updateStockItemFromDto(UpdateStockItemRequestDto request, @MappingTarget StockItem stockItem);
}
