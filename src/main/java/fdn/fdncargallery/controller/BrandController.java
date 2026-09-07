package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.IBrandController;
import fdn.fdncargallery.dto.brand.BrandResponseDto;
import fdn.fdncargallery.dto.brand.CreateBrandRequestDto;
import fdn.fdncargallery.service.interfaces.IBrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN')")
public class BrandController implements IBrandController {

    private final IBrandService brandService;

    @PostMapping("/create_brand")
    public ResponseEntity<BrandResponseDto> createBrand(@Valid @RequestBody CreateBrandRequestDto createBrandRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.createBrand(createBrandRequestDto));
    }

    @GetMapping("/list_brand")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN', 'MANAGER', 'SALES_REP')")
    public ResponseEntity<List<BrandResponseDto>> findAllBrands() {
        return ResponseEntity.ok(brandService.findAllBrands());
    }
}
