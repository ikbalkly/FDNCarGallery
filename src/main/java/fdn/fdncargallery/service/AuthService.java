package fdn.fdncargallery.service;

import fdn.fdncargallery.dto.auth.AuthRequest;
import fdn.fdncargallery.dto.auth.AuthResponse;
import fdn.fdncargallery.dto.auth.ChangePasswordRequestDto;
import fdn.fdncargallery.dto.auth.RefreshTokenRequestDto;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.RefreshToken;
import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.jwt.JwtService;
import fdn.fdncargallery.repository.IEmployeeRepository;
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
    private final IEmployeeRepository employeeRepository;
    private final JwtService jwtService;
    private final IRefreshTokenService refreshTokenService;
    private final SecurityService securityService;
    private final PasswordEncoder passwordEncoder;

    /*
    * Giriş yapma metodu
    * request'den gelen username ve password doğrulanır
    * */
    public AuthResponse login(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            BaseEmployee employee = employeeRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, authRequest.getUsername())));

            String accessToken = jwtService.generateToken(employee);
            RefreshToken savedRefreshToken = refreshTokenService.createRefreshToken(employee);

            return new AuthResponse(accessToken, savedRefreshToken.getRefreshToken(), employee.isFirstLogin());

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

        BaseEmployee employee = existing.getEmployee();
        if (employee == null) {
            throw new BaseException(new ErrorMessage(MessageType.INVALID_TOKEN, "Refresh token bir personele bağlı değil"));
        }

        if (!employee.isEnabled()) {
            throw new BaseException(new ErrorMessage(MessageType.ACCOUNT_DISABLED, employee.getUsername()));
        }

        RefreshToken rotated = refreshTokenService.rotate(existing);
        String accessToken = jwtService.generateToken(employee);

        log.info("Token yenilendi. username: {}", employee.getUsername());
        return new AuthResponse(accessToken, rotated.getRefreshToken(), employee.isFirstLogin());
    }


    @Transactional
    @Override
    public void logout(RefreshTokenRequestDto refreshTokenRequestDto) {
        refreshTokenService.logout(refreshTokenRequestDto.getRefreshToken());
    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordRequestDto changePasswordRequestDto) {

        BaseEmployee currentEmployee = securityService.getCurrentEmployee();

        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(), currentEmployee.getPassword())) {
            throw new BaseException(new ErrorMessage(MessageType.BAD_CREDENTIALS, "Mevcut şifre hatalı."));
        }

        if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getConfirmPassword())) {
            throw new BaseException(new ErrorMessage(MessageType.PASSWORD_CONFIRMATION_MISMATCH, null));
        }

        if (passwordEncoder.matches(changePasswordRequestDto.getNewPassword(), currentEmployee.getPassword())) {
            throw new BaseException(new ErrorMessage(MessageType.NEW_PASSWORD_SAME_AS_OLD, null));
        }

        currentEmployee.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
        currentEmployee.setFirstLogin(false);
        employeeRepository.save(currentEmployee);

        log.info("Şifre değiştirildi. username: {}", currentEmployee.getUsername());
    }


}
