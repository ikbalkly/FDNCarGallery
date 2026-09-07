package fdn.fdncargallery.dto.brand;

import fdn.fdncargallery.dto.BaseEntityResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BrandResponseDto extends BaseEntityResponseDto {

    private String brandName;
}
