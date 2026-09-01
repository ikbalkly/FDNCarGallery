package fdn.fdncargallery.entity;

import fdn.fdncargallery.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employees")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "employee_type", discriminatorType = DiscriminatorType.STRING, length = 50)
public abstract class BaseEmployee extends BaseEntity implements UserDetails {

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

    // işe giriş tarihi
    @Column(nullable = false)
    private LocalDate hireDate = LocalDate.now();

    // işten çıkış tarihi
    @Column(nullable = true)
    private LocalDate terminationDate;

    // kullanıcı adı
    @Column(nullable = false, unique = true, updatable = false)
    private String username;

    // şifresi
    @Column(nullable = false)
    private String password;

    // mailli -> şirket veya bireysel
    @Column(nullable = false, unique = true)
    private String email;

    // rolü
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ilk giriş kontolü -> default şifreyi değiştirmek için
    @Column(nullable = false)
    private boolean isFirstLogin = true;

    // adres -> personel satırına gömülür, ayrı tablo yok
    @Embedded
    private Address address;

    // birçok employee tek bir şubede çalışabilir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Branch branch;

    // mapper, istemci tarih göndermediğinde alan varsayılanını null ile ezer;
    // kayıt anında hala boşsa işe giriş bugün kabul edilir
    @PrePersist
    private void applyHireDateDefault() {
        if (hireDate == null) {
            hireDate = LocalDate.now();
        }
    }

    // personelin rolü tek yetkisidir -> SUPER_ADMIN, BRANCH_ADMIN, MANAGER...
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    // pasife alınan personel sisteme giremez.
    // UserDetails.isEnabled() varsayılanı true döndüğü için bu override şart:
    // olmazsa active=false yapılan personel giriş yapmaya devam eder.
    @Override
    public boolean isEnabled() {
        return active;
    }
}
