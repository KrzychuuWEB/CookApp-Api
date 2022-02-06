package pl.ecookhub.api.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.ecookhub.api.entity.ResetPassword;
import pl.ecookhub.api.entity.User;
import pl.ecookhub.api.exception.BadRequestException;
import pl.ecookhub.api.model.user.ResetPasswordModel;
import pl.ecookhub.api.repository.ResetPasswordRepository;
import pl.ecookhub.api.service.mail.SendMailService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserPasswordResetService {

    private final SendMailService sendMailService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ResetPasswordRepository resetPasswordRepository;

    @Transactional
    public int generateNewResetPasswordTokenAndSendMail(String userEmail) {
        User user = userService.getUserByEmail(userEmail);

        deleteResetTokenIfUserHasCreateNewToken(user);

        int token = generateToken();

        ResetPassword resetPassword = new ResetPassword();
        resetPassword.setToken(token);
        resetPassword.setUser(user);

        resetPasswordRepository.save(resetPassword);

        sendMailWithResetPasswordToken(resetPassword.getUser().getEmail(), token);

        return resetPassword.getToken();
    }

    @Transactional
    public void resetPassword(ResetPasswordModel resetPasswordModel) {
        ResetPassword resetPassword = resetPasswordRepository.findByToken(resetPasswordModel.getToken());

        if (resetPassword == null) {
            throw new BadRequestException("The reset token is incorrect");
        }

        if (!Objects.equals(resetPassword.getUser().getEmail(), resetPasswordModel.getEmail())) {
            throw new BadRequestException("Such e-mail does not have such a token assigned to it");
        }

        if (LocalDateTime.now().isAfter(resetPassword.getExpiryDate())) {
            throw new BadRequestException("Token has been expired!");
        }

        resetPassword.getUser().setPassword(passwordEncoder.encode(resetPasswordModel.getPassword()));

        resetPasswordRepository.deleteByUserId(resetPassword.getUser().getId());
    }

    private int generateToken() {
        Random random = new Random();
        return random.nextInt(999999);
    }

    private void deleteResetTokenIfUserHasCreateNewToken(User user) {
        if (user.getResetPasswords() != null) {
            resetPasswordRepository.deleteByUserId(user.getId());
        }
    }

    private void sendMailWithResetPasswordToken(String email, int token) {
        sendMailService.sendNoReplyEmail(email, "Twój token resetowania hasła", "Token: " + token);
    }
}
