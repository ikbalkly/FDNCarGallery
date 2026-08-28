package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.auth.AuthRequest;
import fdn.fdncargallery.dto.auth.AuthResponse;
import fdn.fdncargallery.dto.auth.ChangePasswordRequestDto;
import fdn.fdncargallery.dto.auth.RefreshTokenRequestDto;
import fdn.fdncargallery.entity.RefreshToken;
import fdn.fdncargallery.entity.UserAccount;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.jwt.JwtService;
import fdn.fdncargallery.repository.IUserAccountRepository;
import fdn.fdncargallery.service.interfaces.IAuthService;
import fdn.fdncargallery.service.interfaces.IRefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final IUserAccountRepository userAccountRepository;
    private final JwtService jwtService;
    private final IRefreshTokenService refreshTokenService;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;

    /*
    * Giriş yapma meetodu
    * request'den gelen username ve password doğrulanır
    *
    * */
    public AuthResponse login(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            UserAccount userAccount = userAccountRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, authRequest.getUsername())));

            String accessToken = jwtService.generateToken(userAccount);
            RefreshToken savedRefreshToken = refreshTokenService.createRefreshToken(userAccount);

            return new AuthResponse(accessToken, savedRefreshToken.getRefreshToken(), userAccount.isFirstLogin());

        } catch (DisabledException e) {
            log.warn("Pasif hesapla giriş denemesi. username: {}", authRequest.getUsername());
            throw new BaseException(new ErrorMessage(MessageType.ACCOUNT_DISABLED, null));

        } catch (BadCredentialsException e) {
            throw new BaseException(new ErrorMessage(MessageType.BAD_CREDENTIALS, null));

        } catch (BaseException e) {
            throw e;

        } catch (Exception e) {
            log.error("Giriş sırasında beklenmeyen hata. username: {}", authRequest.getUsername(), e);
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION, null));
        }
    }

    @Transactional
    @Override
    public AuthResponse refreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {

        RefreshToken existing = refreshTokenService.validate(refreshTokenRequestDto.getRefreshToken());

        UserAccount userAccount = existing.getUserAccount();
        if (userAccount == null) {
            throw new BaseException(new ErrorMessage(MessageType.INVALID_TOKEN, "Refresh token bir hesaba bağlı değil"));
        }

        if (!userAccount.isEnabled()) {
            throw new BaseException(new ErrorMessage(MessageType.ACCOUNT_DISABLED, userAccount.getUsername()));
        }

        RefreshToken rotated = refreshTokenService.rotate(existing);
        String accessToken = jwtService.generateToken(userAccount);

        log.info("Token yenilendi. username: {}", userAccount.getUsername());
        return new AuthResponse(accessToken, rotated.getRefreshToken(), userAccount.isFirstLogin());
    }


    @Transactional
    @Override
    public void logout(RefreshTokenRequestDto refreshTokenRequestDto) {
        refreshTokenService.logout(refreshTokenRequestDto.getRefreshToken());
    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordRequestDto changePasswordRequestDto) {

        UserAccount currentUser = securityService.getUserAccount();

        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(), currentUser.getPassword())) {
            throw new BaseException(new ErrorMessage(MessageType.BAD_CREDENTIALS, "Mevcut şifre hatalı."));
        }

        if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getConfirmPassword())) {
            throw new BaseException(new ErrorMessage(MessageType.PASSWORD_CONFIRMATION_MISMATCH, null));
        }

        if (passwordEncoder.matches(changePasswordRequestDto.getNewPassword(), currentUser.getPassword())) {
            throw new BaseException(new ErrorMessage(MessageType.NEW_PASSWORD_SAME_AS_OLD, null));
        }

        currentUser.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
        currentUser.setFirstLogin(false);
        userAccountRepository.save(currentUser);

        log.info("Şifre değiştirildi. username: {}", currentUser.getUsername());
    }


}