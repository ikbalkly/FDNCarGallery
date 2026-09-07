package fdn.fdncargallery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    // il
    @Column(nullable = false)
    private String city;

    // ilçe
    @Column(nullable = false)
    private String district;

    // mahalle
    @Column(nullable = false)
    private String neighborhood;

    // sokak / cadde
    @Column(nullable = false)
    private String street;

    // bina / site adı -> "Yıldız Apt.", "Meydan Sitesi". Her adreste olmaz.
    @Column(nullable = true)
    private String buildingName;

    // bina no (dış kapı no)
    @Column(nullable = true, length = 10)
    private String buildingNo;

    // daire no (iç kapı no)
    @Column(nullable = true, length = 10)
    private String doorNo;

    // posta kodu
    @Column(nullable = true, length = 5)
    private String zipCode;

    @Transient
    public String getFullAddress() {
        return Stream.of(
                        neighborhood == null ? null : neighborhood + " Mah.",
                        street == null ? null : street + " Sok.",
                        buildingName,
                        buildingNo == null ? null : "No: " + buildingNo,
                        doorNo == null ? null : "D: " + doorNo,
                        (district == null || city == null) ? null : district + "/" + city,
                        zipCode)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));
    }
}
