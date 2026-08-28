package fdn.fdncargallery.entity;

import fdn.fdncargallery.enums.BodyType;
import fdn.fdncargallery.enums.Drivetrain;
import fdn.fdncargallery.enums.FuelType;
import fdn.fdncargallery.enums.TransmissionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseEntity {

    // Aracın değişmez kimliği. Bir fiziksel araç sistemde YALNIZCA BİR KEZ bulunur.
    @Column(nullable = false, unique = true, length = 17, updatable = false)
    private String vin;

    // marka
    @Column(nullable = false)
    private String brand;

    // model
    @Column(nullable = false)
    private String model;

    // seri
    @Column(nullable = false)
    private String series;

    // model yılı
    @Column(nullable = false)
    private Integer modelYear;

    // motor kapasitesi
    private Integer engineCapacity;

    // motor gücü
    private Integer enginePower;

    // yakıt tipi
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;

    // vites türü
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransmissionType transmission;

    // kasa tipi
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BodyType bodyType;

    // çekiş türü
    @Enumerated(EnumType.STRING)
    private Drivetrain drivetrain;

    // Aynı aracın galeriden geçtiği tüm döngüler
    @OneToMany(mappedBy = "vehicle")
    private List<StockItem> stockItems;
}