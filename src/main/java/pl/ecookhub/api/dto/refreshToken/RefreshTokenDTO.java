package pl.ecookhub.api.dto.refreshToken;

import lombok.Getter;
import pl.ecookhub.api.dto.user.UserDTO;
import pl.ecookhub.api.entity.RefreshToken;
import pl.ecookhub.api.entity.User;

import java.time.LocalDateTime;

@Getter
public class RefreshTokenDTO {
    private long id;
    private final String token;
    private final LocalDateTime expiryDate;
    private final UserDTO user;

    public RefreshTokenDTO(RefreshToken refreshToken) {
        this.id = refreshToken.getId();
        this.token = refreshToken.getToken();
        this.expiryDate = refreshToken.getExpiryDate();
        this.user = new UserDTO(refreshToken.getUser());
    }

    public RefreshTokenDTO(String token, LocalDateTime getExpiryDate, User user) {
        this.token = token;
        this.expiryDate = getExpiryDate;
        this.user = new UserDTO(user);
    }
}
