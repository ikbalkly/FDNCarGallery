package fdn.fdncargallery.jwt;

import fdn.fdncargallery.entity.BaseEmployee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final long ACCESS_TOKEN_VALIDITY_MS = 1000 * 60 * 15L; // 15 dk
    private final SecretKey secretKey;

    public JwtService(@Value("${fdn.jwt.secret:}") String configuredSecret) {
        if (configuredSecret == null || configuredSecret.isBlank()) {
            throw new IllegalStateException(
                    "FDN_JWT_SECRET tanımlı değil. İmzalama anahtarı olmadan uygulama başlatılamaz.");
        }
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(configuredSecret));
    }

    public String generateToken(BaseEmployee employee) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", employee.getRole().name());
        claims.put("isFirstLogin", employee.isFirstLogin());
        if (employee.getBranch() != null) {
            claims.put("branchId", employee.getBranch().getId());
        }

        return Jwts
                .builder()
                .subject(employee.getUsername())
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_MS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T parseToken(String token, Function<Claims, T> claimsTFunction) {
        Claims body = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsTFunction.apply(body);
    }

    public String getUsernameFromToken(String token) {
        return parseToken(token, Claims::getSubject);
    }

    public Boolean isTokenExpired(String token) {
        return parseToken(token, Claims::getExpiration).before(new Date());
    }

    public boolean extractIsFirstLogin(String token) {
        Boolean isFirstLogin = parseToken(token, claims -> claims.get("isFirstLogin", Boolean.class));
        return isFirstLogin != null ? isFirstLogin : false;
    }
}

