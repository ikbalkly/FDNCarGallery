package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.RefreshToken;

public interface IRefreshTokenService {

    public RefreshToken createRefreshToken(BaseEmployee employee);

    /**
     * Token'ı bulur ve süresini doğrular. Süresi dolmuşsa kaydı siler ve hata
     * fırlatır; bulunamazsa da hata fırlatır. Başarılıysa kaydın kendisini döner.
     */
    public RefreshToken validate(String refreshToken);

    /**
     * Kullanılan token'ı düşürüp yerine yenisini üretir (rotation).
     * Refresh token'lar tek kullanımlıktır: sızan bir token ikinci kez
     * kullanılamasın diye her yenilemede değişir.
     */
    public RefreshToken rotate(RefreshToken current);

    /**
     * Oturumu kapatır: refresh token kaydını siler, böylece kullanıcı yeni
     * access token alamaz. SESSİZDİR — token bulunamazsa hata fırlatmaz,
     * çünkü "zaten çıkmış olmak" bir hata durumu değildir.
     */
    public void logout(String refreshToken);
}
