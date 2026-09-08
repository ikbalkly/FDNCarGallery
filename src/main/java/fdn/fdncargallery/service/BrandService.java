package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.brand.BrandResponseDto;
import fdn.fdncargallery.dto.brand.CreateBrandRequestDto;
import fdn.fdncargallery.entity.Brand;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IBrandMapper;
import fdn.fdncargallery.repository.IBrandRepository;
import fdn.fdncargallery.service.interfaces.IBrandService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService implements IBrandService {

    private final IBrandRepository brandRepository;
    private final IBrandMapper brandMapper;

    @Transactional
    @Override
    public BrandResponseDto createBrand(CreateBrandRequestDto request) {

        String brandName = request.getBrandName().trim();

        // Büyük/küçük harf farkı yeni marka sayılmaz: "ford" ile "Ford" aynı markadır.
        // select * from fdncargallery.brands where upper(brand_name) = upper(?)
        if (brandRepository.existsByBrandNameIgnoreCase(brandName)) {
            throw new BaseException(new ErrorMessage(MessageType.BRAND_ALREADY_EXISTS, brandName));
        }

        Brand brand = brandMapper.toEntity(request);
        brand.setBrandName(brandName);

        Brand saved = brandRepository.saveAndFlush(brand);

        log.info("Yeni marka tanımlandı. id: {}, marka: {}", saved.getId(), saved.getBrandName());
        return brandMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public List<BrandResponseDto> findAllBrands() {
        return brandRepository.findAllByOrderByBrandNameAsc()
                .stream()
                .map(brandMapper::toResponse)
                .toList();
    }

    @Override
    public Brand getBrandEntityById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRAND_NOT_FOUND, id.toString())));
    }

    @Override
    public Brand getBrandEntityByName(String brandName) {
        return brandRepository.findByBrandNameIgnoreCase(brandName.trim())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.BRAND_NOT_FOUND, brandName)));
    }
}
