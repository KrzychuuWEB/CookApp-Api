package pl.ecookhub.api.exception.refreshToken;

public class RefreshTokenHasAlreadyUsedException extends RuntimeException {
    public RefreshTokenHasAlreadyUsedException(String message) {
        super(message);
    }
}
