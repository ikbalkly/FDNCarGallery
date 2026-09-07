package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.brand.BrandResponseDto;
import fdn.fdncargallery.dto.brand.CreateBrandRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBrandController {

    public ResponseEntity<BrandResponseDto> createBrand(CreateBrandRequestDto createBrandRequestDto);

    public ResponseEntity<List<BrandResponseDto>> findAllBrands();
}
