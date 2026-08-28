package fdn.fdncargallery.dto.vehicle;

import fdn.fdncargallery.dto.BaseEntityResponseDto;
import fdn.fdncargallery.enums.BodyType;
import fdn.fdncargallery.enums.Drivetrain;
import fdn.fdncargallery.enums.FuelType;
import fdn.fdncargallery.enums.TransmissionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VehicleResponseDto extends BaseEntityResponseDto {

    private String vin;
    private String brand;
    private String model;
    private String series;
    private Integer modelYear;

    private Integer engineCapacity;
    private Integer enginePower;

    private FuelType fuelType;
    private TransmissionType transmission;
    private BodyType bodyType;
    private Drivetrain drivetrain;

    public String getBrandAndModel() {
        return brand + " " + model;
    }
}