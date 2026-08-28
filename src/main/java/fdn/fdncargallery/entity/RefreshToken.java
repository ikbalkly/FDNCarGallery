package fdn.fdncargallery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends BaseEntity {

    // token
    @Column(nullable = false, unique = true)
    private String refreshToken;

    // Takvim tarihi değil, bir zaman damgası -> Instant saat dilimi belirsizliği taşımaz
    @Column(nullable = false)
    private Instant expiryDate;

    // kullanıcı hesabı
    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount userAccount;
}
