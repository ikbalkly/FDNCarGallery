package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.IModelController;
import fdn.fdncargallery.dto.model.CreateModelRequestDto;
import fdn.fdncargallery.dto.model.ModelResponseDto;
import fdn.fdncargallery.service.interfaces.IModelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN')")
public class ModelController implements IModelController {

    private final IModelService modelService;

    @PostMapping("/create_model")
    public ResponseEntity<ModelResponseDto> createModel(@Valid @RequestBody CreateModelRequestDto createModelRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(modelService.createModel(createModelRequestDto));
    }

    @GetMapping("/list_model")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN', 'MANAGER', 'SALES_REP')")
    public ResponseEntity<List<ModelResponseDto>> findAllModels() {
        return ResponseEntity.ok(modelService.findAllModels());
    }

    @GetMapping("/list_model/brand/{brandId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'BRANCH_ADMIN', 'MANAGER', 'SALES_REP')")
    public ResponseEntity<List<ModelResponseDto>> findModelsByBrandId(@PathVariable Long brandId) {
        return ResponseEntity.ok(modelService.findModelsByBrandId(brandId));
    }
}
