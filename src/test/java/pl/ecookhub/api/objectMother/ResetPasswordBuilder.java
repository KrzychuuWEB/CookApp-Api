package pl.ecookhub.api.objectMother;

import pl.ecookhub.api.entity.ResetPassword;
import pl.ecookhub.api.entity.User;

import java.time.LocalDateTime;

public class ResetPasswordBuilder {

    private long id = 1;
    private User user;
    private int token = 123456;
    private LocalDateTime expiryDate;

    ResetPasswordBuilder() {
        this.user = UserBuilder.newUser().build();
        this.expiryDate = LocalDateTime.now();
    }

    public static ResetPasswordBuilder newResetPassword() {
        return new ResetPasswordBuilder();
    }

    public ResetPasswordBuilder withExpiryDate(LocalDateTime expiry) {
        this.expiryDate = expiry;
        return this;
    }

    public ResetPasswordBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public ResetPasswordBuilder but() {
        return ResetPasswordBuilder
                .newResetPassword()
                .withExpiryDate(expiryDate)
                ;
    }

    public ResetPassword build() {
        ResetPassword resetPassword = new ResetPassword();
        resetPassword.setId(id);
        resetPassword.setUser(user);
        resetPassword.setToken(token);
        resetPassword.setExpiryDate(expiryDate);

        return resetPassword;
    }
}
