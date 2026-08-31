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
@Table(name = "employees")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "employee_type", discriminatorType = DiscriminatorType.STRING, length = 50)
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

    // adres -> personel satırına gömülür, ayrı tablo yok
    @Embedded
    private Address address;

    // birçok employee tek bir şubede çalışabilir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Branch branch;

    // kullanıcı hesabı
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "employee")
    private UserAccount userAccount;
}
