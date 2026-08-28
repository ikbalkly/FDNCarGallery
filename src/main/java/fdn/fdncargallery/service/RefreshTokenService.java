package fdn.fdncargallery.service;

import fdn.fdncargallery.entity.RefreshToken;
import fdn.fdncargallery.entity.UserAccount;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.repository.IRefreshTokenRepository;
import fdn.fdncargallery.service.interfaces.IRefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService implements IRefreshTokenService {

    private static final int VALIDITY_DAYS = 7;

    private final IRefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(UserAccount userAccount) {

            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUserAccount(userAccount);
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plus(VALIDITY_DAYS, ChronoUnit.DAYS));

            return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken validate(String refreshToken) {

        RefreshToken existing = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.INVALID_TOKEN, "Refresh token geçersiz")));

        if (existing.getExpiryDate().isBefore(Instant.now())) {
            // süresi dolmuş olanları siler
            refreshTokenRepository.delete(existing);
            throw new BaseException(new ErrorMessage(MessageType.REFRESH_TOKEN_EXPIRED, null));
        }

        return existing;
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {

        long deleted = refreshTokenRepository.deleteByRefreshToken(refreshToken);

        if (deleted > 0) {
            log.info("Oturum kapatıldı.");
        } else {
            log.debug("Logout çağrıldı ama eşleşen refresh token yok; kullanıcı zaten çıkmış sayılıyor.");
        }
    }

    @Override
    @Transactional
    public RefreshToken rotate(RefreshToken current) {

        UserAccount owner = current.getUserAccount();
        refreshTokenRepository.delete(current);

        RefreshToken rotated = createRefreshToken(owner);
        log.debug("Refresh token yenilendi. username: {}", owner.getUsername());
        return rotated;
    }
}
