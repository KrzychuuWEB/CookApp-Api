package pl.ecookhub.api.config.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.ecookhub.api.service.security.JWTService;
import pl.ecookhub.api.service.security.RefreshTokenService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;

    public RestAuthenticationSuccessHandler(JWTService jwtService, RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();

        String accessToken = jwtService.createJWT(principal);
        String refreshToken = refreshTokenService.createNewRefreshToken(principal.getId());


        String json = String.format("{\"access_token\": \"%s\", \"refresh_token\": \"%s\"}", accessToken, refreshToken);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
