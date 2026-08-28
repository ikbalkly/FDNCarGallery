package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.stockItem.CreateStockItemRequestDto;
import fdn.fdncargallery.dto.stockItem.StockItemResponseDto;
import fdn.fdncargallery.dto.stockItem.UpdateStockItemRequestDto;
import fdn.fdncargallery.entity.StockItem;

import java.util.List;

public interface IStockItemService {

    StockItemResponseDto createStockItem(CreateStockItemRequestDto createStockItemRequestDto);

    StockItemResponseDto updateStockItem(UpdateStockItemRequestDto updateStockItemRequestDto, Long id);

    StockItemResponseDto findStockItemById(Long id);

    List<StockItemResponseDto> findAllStockItems();

    void deleteStockItem(Long id);

    StockItem getStockItemEntityById(Long id);
}
