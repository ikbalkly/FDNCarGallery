package fdn.fdncargallery.controller;

import fdn.fdncargallery.controller.interfaces.IAuthController;
import fdn.fdncargallery.dto.auth.AuthRequest;
import fdn.fdncargallery.dto.auth.AuthResponse;
import fdn.fdncargallery.dto.auth.ChangePasswordRequestDto;
import fdn.fdncargallery.dto.auth.RefreshTokenRequestDto;
import fdn.fdncargallery.service.interfaces.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements IAuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.login(authRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        authService.changePassword(changePasswordRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequestDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        authService.logout(refreshTokenRequestDto);
        return ResponseEntity.noContent().build();
    }
}
