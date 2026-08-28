package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.stockItem.CreateStockItemRequestDto;
import fdn.fdncargallery.dto.stockItem.StockItemResponseDto;
import fdn.fdncargallery.dto.stockItem.UpdateStockItemRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IStockItemController {

    public ResponseEntity<StockItemResponseDto> createStockItem(CreateStockItemRequestDto createStockItemRequestDto);

    public ResponseEntity<StockItemResponseDto> updateStockItem(UpdateStockItemRequestDto updateStockItemRequestDto, Long id);

    public ResponseEntity<StockItemResponseDto> findStockItemById(Long id);

    public ResponseEntity<List<StockItemResponseDto>> findAllStockItems();

    public ResponseEntity<Void> deleteStockItem(Long id);
}
