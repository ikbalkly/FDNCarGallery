package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseEmployee extends BaseEntity {

    // ad
    @Column(nullable = false)
    private String name;

    // soyad
    @Column(nullable = false)
    private String surname;

    // tc
    @Column(nullable = false, unique = true, length = 11, updatable = false)
    private String identityNumber;

    // telefon numarası
    @Column(nullable = false)
    private String phoneNumber;

    // maaş
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal baseSalary;

    // aktiflik
    @Column(nullable = false)
    private boolean active = true;

    // adres
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Address address;

    // birçok employee tek bir şubede çalışabilir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Branch branch;

    // kullanıcı hesabı
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "employee")
    private UserAccount userAccount;
}
