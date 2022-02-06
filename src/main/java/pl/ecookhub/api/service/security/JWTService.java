package pl.ecookhub.api.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.ecookhub.api.config.jwt.MyUserPrincipal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    private final long expirationTime;
    private final String secret;

    public JWTService(
            @Value("${jwt.expirationTime}") long expirationTime,
            @Value("${jwt.secret}") String secret) {
        this.expirationTime = expirationTime;
        this.secret = secret;
    }

    public String createJWT(MyUserPrincipal principal) {
        Map<String, String> payload = new HashMap<String, String>();
        payload.put("id", principal.getId().toString());
        payload.put("username", principal.getUsername());

        return JWT.create()
                .withPayload(payload)
                .withSubject(principal.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC256(secret));
    }
}
