package fdn.fdncargallery.controller.interfaces;

import fdn.fdncargallery.dto.auth.AuthRequest;
import fdn.fdncargallery.dto.auth.AuthResponse;
import fdn.fdncargallery.dto.auth.ChangePasswordRequestDto;
import fdn.fdncargallery.dto.auth.RefreshTokenRequestDto;
import org.springframework.http.ResponseEntity;

public interface IAuthController {

    public ResponseEntity<AuthResponse> login(AuthRequest authRequest);

    public ResponseEntity<Void> changePassword(ChangePasswordRequestDto changePasswordRequestDto);

    public ResponseEntity<AuthResponse> refreshToken(RefreshTokenRequestDto refreshTokenRequestDto);

    public ResponseEntity<Void> logout(RefreshTokenRequestDto refreshTokenRequestDto);
}
