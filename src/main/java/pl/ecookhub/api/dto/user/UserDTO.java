package pl.ecookhub.api.dto.user;

import lombok.Getter;
import pl.ecookhub.api.entity.RefreshToken;
import pl.ecookhub.api.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class UserDTO {
    private final long id;
    private final String username;
    private final String email;
    private final boolean enabled;
    private final LocalDateTime createAt;
    private final LocalDateTime editedAt;
    private final List<RefreshToken> tokens;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.enabled = user.isEnabled();
        this.createAt = user.getCreatedAt();
        this.editedAt = user.getEditedAt();
        this.tokens = user.getRefreshTokens();
    }
}

