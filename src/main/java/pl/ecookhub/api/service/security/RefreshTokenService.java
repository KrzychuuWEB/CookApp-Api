package pl.ecookhub.api.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.ecookhub.api.entity.RefreshToken;
import pl.ecookhub.api.entity.User;
import pl.ecookhub.api.exception.refreshToken.ExpiredRefreshTokenException;
import pl.ecookhub.api.exception.refreshToken.InvalidRefreshTokenException;
import pl.ecookhub.api.exception.refreshToken.RefreshTokenHasAlreadyUsedException;
import pl.ecookhub.api.repository.RefreshTokenRepository;
import pl.ecookhub.api.service.user.UserService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${refreshToken.expirationTimeMinutes}")
    private int refreshTokenExpirationTime;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public String createNewRefreshToken(long id) {
        User user = userService.getUserById(id);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(getExpiryDate());
        refreshToken.setToken(generateRefreshToken());

        return refreshTokenRepository.save(refreshToken).getToken();
    }

    public String refreshOldToken(String token) {
        checkExpireRefreshTokenAndIfHasNotBeenUsed(token);

        updateUsedOnTrueForRefreshToken(token);

        User user = findUserByRefreshToken(token);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(getExpiryDate());
        refreshToken.setToken(generateRefreshToken());

        return refreshTokenRepository.save(refreshToken).getToken();
    }

    public User findUserByRefreshToken(String token) {
        User user = refreshTokenRepository.findByToken(token).getUser();

        if (user == null) {
            throw new InvalidRefreshTokenException("The refresh token is invalid!");
        }

        return user;
    }

    private String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    private LocalDateTime getExpiryDate() {
        return LocalDateTime.now().plusMinutes(refreshTokenExpirationTime);
    }

    private void updateUsedOnTrueForRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token);

        refreshToken.setUsed(true);
        refreshTokenRepository.save(refreshToken);
    }

    private void checkExpireRefreshTokenAndIfHasNotBeenUsed(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token);

        if (LocalDateTime.now().isAfter(refreshToken.getExpiryDate())) {
            throw new ExpiredRefreshTokenException("This refresh token has been expired");
        } else if (refreshToken.isUsed()) {
            throw new RefreshTokenHasAlreadyUsedException("This refresh token has been used");
        }
    }
}
