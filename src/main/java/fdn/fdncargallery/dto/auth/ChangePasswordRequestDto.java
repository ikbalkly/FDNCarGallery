package fdn.fdncargallery.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequestDto {

    @NotBlank(message = "Mevcut şifre zorunludur")
    private String currentPassword;

    @NotBlank(message = "Yeni şifre zorunludur")
    @Size(min = 8, max = 72, message = "Yeni şifre en az 8, en fazla 72 karakter olmalıdır")
    private String newPassword;

    @NotBlank(message = "Yeni şifre tekrarı zorunludur")
    private String confirmPassword;
}
