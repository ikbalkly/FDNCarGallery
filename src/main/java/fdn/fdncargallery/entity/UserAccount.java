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

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_accounts")
public class  UserAccount extends BaseEntity implements UserDetails {

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

    // sistemde aktif olan kişinin bilgileri
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BaseEmployee employee;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return this.password; // Kesinlikle kendi field'ını dönmeli!
    }

    @Override
    public String getUsername() {
        return this.username; // Kesinlikle kendi field'ını dönmeli!
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return employee == null || employee.isActive();
    }
}
