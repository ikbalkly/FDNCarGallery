package fdn.fdncargallery.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$",
            message = "Şifre en az bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter içermelidir"
    )
    private String newPassword;

    @NotBlank(message = "Yeni şifre tekrarı zorunludur")
    private String confirmPassword;
}
