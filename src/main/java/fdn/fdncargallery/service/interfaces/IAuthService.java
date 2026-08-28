package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.dto.auth.AuthRequest;
import fdn.fdncargallery.dto.auth.AuthResponse;
import fdn.fdncargallery.dto.auth.ChangePasswordRequestDto;
import fdn.fdncargallery.dto.auth.RefreshTokenRequestDto;

public interface IAuthService {
    public AuthResponse login(AuthRequest authRequest);

    public void changePassword(ChangePasswordRequestDto changePasswordRequestDto);

    public AuthResponse refreshToken(RefreshTokenRequestDto refreshTokenRequestDto);

    public void logout(RefreshTokenRequestDto refreshTokenRequestDto);
}
