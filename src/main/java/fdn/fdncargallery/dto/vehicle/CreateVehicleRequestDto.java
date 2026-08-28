package fdn.fdncargallery.dto.vehicle;

import fdn.fdncargallery.enums.BodyType;
import fdn.fdncargallery.enums.Drivetrain;
import fdn.fdncargallery.enums.FuelType;
import fdn.fdncargallery.enums.TransmissionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateVehicleRequestDto {

    @NotBlank(message = "Şasi numarası (VIN) zorunludur")
    @Size(min = 17, max = 17, message = "Şasi numarası tam olarak 17 karakter olmalıdır")
    private String vin;

    @NotBlank(message = "Marka alanı boş bırakılamaz (Örn: Ford)")
    private String brand;

    @NotBlank(message = "Model alanı boş bırakılamaz (Örn: Focus)")
    private String model;

    @NotBlank(message = "Seri alanı boş bırakılamaz")
    private String series;

    @NotNull(message = "Model yılı zorunludur")
    @Min(value = 1900, message = "Model yılı 1900'dan küçük olamaz")
    private Integer modelYear;

    @Min(value = 0, message = "Motor hacmi negatif olamaz")
    private Integer engineCapacity;

    @Min(value = 0, message = "Motor gücü negatif olamaz")
    private Integer enginePower;

    @NotNull(message = "Yakıt tipi zorunludur")
    private FuelType fuelType;

    @NotNull(message = "Vites türü zorunludur")
    private TransmissionType transmission;

    @NotNull(message = "Kasa tipi zorunludur")
    private BodyType bodyType;

    private Drivetrain drivetrain;
}
