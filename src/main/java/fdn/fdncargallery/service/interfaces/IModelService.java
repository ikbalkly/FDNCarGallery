package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.model.CreateModelRequestDto;
import fdn.fdncargallery.dto.model.ModelResponseDto;
import fdn.fdncargallery.entity.Brand;
import fdn.fdncargallery.entity.Model;

import java.util.List;

/**
 * Model referans verisi. Her model bir markaya bağlıdır ve adı yalnızca
 * markası içinde tekildir.
 * <p>
 * Silme ucu bilinçli olarak YOKTUR: bkz. IBrandService.
 */
public interface IModelService {

    ModelResponseDto createModel(CreateModelRequestDto request);

    List<ModelResponseDto> findAllModels();

    List<ModelResponseDto> findModelsByBrandId(Long brandId);

    // Araç kaydında model adıyla geliyor; markası altında yoksa MODEL_NOT_FOUND.
    Model getModelEntityByBrandAndName(Brand brand, String modelName);
}
