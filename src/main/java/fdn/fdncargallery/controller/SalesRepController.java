package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.ISalesRepController;
import fdn.fdncargallery.dto.employee.ReactivateEmployeeRequestDto;
import fdn.fdncargallery.dto.salesRep.CreateSalesRepRequestDto;
import fdn.fdncargallery.dto.salesRep.SalesRepResponseDto;
import fdn.fdncargallery.dto.salesRep.UpdateSalesRepRequestDto;
import fdn.fdncargallery.service.interfaces.ISalesRepService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salesRep")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN')")
public class SalesRepController implements ISalesRepController {

    private final ISalesRepService salesRepService;

    @PostMapping("/create_salesRep")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN', 'MANAGER')")
    @Override
    public ResponseEntity<SalesRepResponseDto> createSalesRep(@Valid @RequestBody CreateSalesRepRequestDto requestDto) {
        SalesRepResponseDto salesRep = salesRepService.createSalesRep(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salesRep);
    }

    @GetMapping("/list_salesRep/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN', 'MANAGER', 'SALES_REP')")
    @Override
    public ResponseEntity<SalesRepResponseDto> findSalesRepById(@PathVariable Long id) {
        return ResponseEntity.ok(salesRepService.findSalesRepById(id));
    }

    @GetMapping("/list_allSalesRep")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN', 'MANAGER')")
    @Override
    public ResponseEntity<List<SalesRepResponseDto>> findAllSalesReps() {
        return ResponseEntity.ok(salesRepService.findAllSalesReps());
    }

    @PutMapping("/update_salesRep/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN', 'MANAGER')")
    @Override
    public ResponseEntity<SalesRepResponseDto> updateSalesRep(@Valid @RequestBody UpdateSalesRepRequestDto requestDto,
                                                              @PathVariable Long id) {
        return ResponseEntity.ok(salesRepService.updateSalesRep(requestDto, id));
    }

    @DeleteMapping("/delete_salesRep/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN', 'MANAGER')")
    @Override
    public ResponseEntity<Void> deleteSalesRep(@PathVariable Long id) {
        salesRepService.deleteSalesRep(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reactivate_salesRep/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN', 'MANAGER')")
    @Override
    public ResponseEntity<SalesRepResponseDto> reactivateSalesRep(@Valid @RequestBody ReactivateEmployeeRequestDto request,
                                                                  @PathVariable Long id) {
        return ResponseEntity.ok(salesRepService.reactivateSalesRep(request, id));
    }
}
