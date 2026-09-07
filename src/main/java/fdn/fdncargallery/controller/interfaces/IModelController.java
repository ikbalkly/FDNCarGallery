package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.model.CreateModelRequestDto;
import fdn.fdncargallery.dto.model.ModelResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IModelController {

    public ResponseEntity<ModelResponseDto> createModel(CreateModelRequestDto createModelRequestDto);

    public ResponseEntity<List<ModelResponseDto>> findAllModels();

    public ResponseEntity<List<ModelResponseDto>> findModelsByBrandId(Long brandId);
}
