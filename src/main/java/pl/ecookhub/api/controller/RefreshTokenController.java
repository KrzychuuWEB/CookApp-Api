package pl.ecookhub.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.ecookhub.api.config.jwt.MyUserPrincipal;
import pl.ecookhub.api.model.refreshToken.SaveRefreshTokenModel;
import pl.ecookhub.api.service.security.JWTService;
import pl.ecookhub.api.service.security.RefreshTokenService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final JWTService jwtService;

    @PostMapping("/token/refresh")
    public ResponseEntity<Map<String, String>> newToken(@RequestBody SaveRefreshTokenModel saveRefreshTokenModel) {
        Map<String, String> response = new HashMap<>();
        String refreshToken = refreshTokenService.refreshOldToken(saveRefreshTokenModel.getToken());

        MyUserPrincipal principal = new MyUserPrincipal(refreshTokenService.findUserByRefreshToken(saveRefreshTokenModel.getToken()));
        String accessToken = jwtService.createJWT(principal);

        response.put("access_token", accessToken);
        response.put("refresh_token", refreshToken);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
