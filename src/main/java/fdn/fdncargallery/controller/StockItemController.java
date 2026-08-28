package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.IStockItemController;
import fdn.fdncargallery.dto.stockItem.CreateStockItemRequestDto;
import fdn.fdncargallery.dto.stockItem.StockItemResponseDto;
import fdn.fdncargallery.dto.stockItem.UpdateStockItemRequestDto;
import fdn.fdncargallery.service.interfaces.IStockItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-items")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'BRANCH_ADMIN', 'MANAGER')")
public class StockItemController implements IStockItemController {

    private final IStockItemService stockItemService;

    @PostMapping("/create_stock_item")
    public ResponseEntity<StockItemResponseDto> createStockItem(@Valid @RequestBody CreateStockItemRequestDto createStockItemRequestDto) {
        StockItemResponseDto response = stockItemService.createStockItem(createStockItemRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update_stock_item/{id}")
    public ResponseEntity<StockItemResponseDto> updateStockItem(@Valid @RequestBody UpdateStockItemRequestDto updateStockItemRequestDto,
                                                                @PathVariable Long id) {
        return ResponseEntity.ok(stockItemService.updateStockItem(updateStockItemRequestDto, id));
    }

    @GetMapping("/list_stock_item/{id}")
    public ResponseEntity<StockItemResponseDto> findStockItemById(@PathVariable Long id) {
        return ResponseEntity.ok(stockItemService.findStockItemById(id));
    }

    @GetMapping("/list_stock_item")
    public ResponseEntity<List<StockItemResponseDto>> findAllStockItems() {
        return ResponseEntity.ok(stockItemService.findAllStockItems());
    }

    @DeleteMapping("/delete_stock_item/{id}")
    public ResponseEntity<Void> deleteStockItem(@PathVariable Long id) {
        stockItemService.deleteStockItem(id);
        return ResponseEntity.noContent().build();
    }
}
