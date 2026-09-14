package fdn.fdncargallery.service.interfaces;

import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.entity.RefreshToken;

public interface IRefreshTokenService {

    public RefreshToken createRefreshToken(BaseEmployee employee);

    public RefreshToken validate(String refreshToken);

    public RefreshToken rotate(RefreshToken current);

    public void logout(String refreshToken);

    public void revokeAllTokens(BaseEmployee employee);
}
