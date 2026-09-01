package fdn.fdncargallery.jwt;


import fdn.fdncargallery.config.SecurityConfig;
import fdn.fdncargallery.entity.BaseEmployee;
import fdn.fdncargallery.exception.MessageType;
import fdn.fdncargallery.handler.ApiErrorWriter;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private final ApiErrorWriter apiErrorWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                String username = jwtService.getUsernameFromToken(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (userDetails != null && !jwtService.isTokenExpired(token)) {

                        if (!userDetails.isEnabled()) {
                            apiErrorWriter.write(request, response, MessageType.ACCOUNT_DISABLED);
                            return;
                        }
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());

                        authenticationToken.setDetails(userDetails);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                        boolean isFirstLogin = ((BaseEmployee) userDetails).isFirstLogin();
                        String requestUri = request.getRequestURI();

                        // eğer kişi hala isFirstLogin=true ve tıkladığı adres şifre değiştirme değilse hata fırtlat
                        // kişiyi şifre değiştirmeye zorla
                        if (isFirstLogin && !requestUri.equals(SecurityConfig.CHANGE_PASSWORD)) {
                            apiErrorWriter.write(request, response, MessageType.PASSWORD_CHANGE_REQUIRED);
                            return;
                        }
                    }
                }
            } catch (ExpiredJwtException ex) {
                apiErrorWriter.write(request, response, MessageType.TOKEN_IS_EXPIRE);
                return;
            } catch (Exception e) {

                logger.warn("Token doğrulanamadı: " + e.getClass().getSimpleName() + " - " + e.getMessage(), e);
                apiErrorWriter.write(request, response, MessageType.INVALID_TOKEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
