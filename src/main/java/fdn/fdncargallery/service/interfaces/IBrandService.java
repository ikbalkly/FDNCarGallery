package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.brand.BrandResponseDto;
import fdn.fdncargallery.dto.brand.CreateBrandRequestDto;
import fdn.fdncargallery.entity.Brand;

import java.util.List;

/**
 * Marka referans verisi.
 * <p>
 * Silme ucu bilinçli olarak YOKTUR: markayı silmek onu kullanan araçların
 * ilişkisini koparır (@SQLRestriction nedeniyle satır sorgulardan düşer).
 */
public interface IBrandService {

    BrandResponseDto createBrand(CreateBrandRequestDto request);

    List<BrandResponseDto> findAllBrands();

    Brand getBrandEntityById(Long id);

    // Araç kaydında marka adıyla geliyor; tanımlı değilse BRAND_NOT_FOUND.
    Brand getBrandEntityByName(String brandName);
}
