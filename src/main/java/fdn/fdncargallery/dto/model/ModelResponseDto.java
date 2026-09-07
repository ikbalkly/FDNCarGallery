package fdn.fdncargallery.dto.model;

import fdn.fdncargallery.dto.BaseEntityResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ModelResponseDto extends BaseEntityResponseDto {

    private String modelName;

    private Long brandId;
    private String brandName;
}
