package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.model.CreateModelRequestDto;
import fdn.fdncargallery.dto.model.ModelResponseDto;
import fdn.fdncargallery.entity.Brand;
import fdn.fdncargallery.entity.Model;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.mapper.IModelMapper;
import fdn.fdncargallery.repository.IModelRepository;
import fdn.fdncargallery.service.interfaces.IBrandService;
import fdn.fdncargallery.service.interfaces.IModelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelService implements IModelService {

    private final IModelRepository modelRepository;
    private final IModelMapper modelMapper;
    private final IBrandService brandService;

    @Transactional
    @Override
    public ModelResponseDto createModel(CreateModelRequestDto request) {

        // Marka gerçekten var mı?
        Brand brand = brandService.getBrandEntityById(request.getBrandId());

        String modelName = request.getModelName().trim();

        if (modelRepository.existsByBrandIdAndModelNameIgnoreCase(brand.getId(), modelName)) {
            throw new BaseException(new ErrorMessage(MessageType.MODEL_ALREADY_EXISTS, brand.getBrandName() + " " + modelName));
        }

        Model model = modelMapper.toEntity(request);
        model.setModelName(modelName);
        model.setBrand(brand);

        Model saved = modelRepository.saveAndFlush(model);

        log.info("Yeni model tanımlandı. id: {}, marka: {}, model: {}", saved.getId(), brand.getBrandName(), saved.getModelName());
        return modelMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public List<ModelResponseDto> findAllModels() {
        return modelRepository.findAllByOrderByModelNameAsc()
                .stream()
                .map(modelMapper::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public List<ModelResponseDto> findModelsByBrandId(Long brandId) {

        Brand brand = brandService.getBrandEntityById(brandId);

        return modelRepository.findAllByBrandIdOrderByModelNameAsc(brand.getId())
                .stream()
                .map(modelMapper::toResponse)
                .toList();
    }

    @Override
    public Model getModelEntityByBrandAndName(Brand brand, String modelName) {
        return modelRepository.findByBrandIdAndModelNameIgnoreCase(brand.getId(), modelName.trim())
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessageType.MODEL_NOT_FOUND, brand.getBrandName() + " " + modelName)));
    }
}
