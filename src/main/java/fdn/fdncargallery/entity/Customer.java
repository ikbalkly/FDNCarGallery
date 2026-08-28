package fdn.fdncargallery.entity;

import fdn.fdncargallery.enums.CustomerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customers")
public class Customer extends BaseEntity {

    // müşterinin tipi ( kurumsal- bireysel)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType customerType;

    // tc
    @Column(nullable = false, unique = true, length = 11)
    private String identityNumber;

    // ad
    @Column(nullable = false)
    private String firstName;

    // soyad
    @Column(nullable = false)
    private String lastName;

    // telefon numarası
    @Column(nullable = false)
    private String phoneNumber;

    // email
    private String email;

    // adres
    @OneToOne(cascade = CascadeType.ALL)
    private Address address;
}
