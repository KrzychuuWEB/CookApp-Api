package pl.ecookhub.api.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecookhub.api.entity.User;
import pl.ecookhub.api.exception.BadRequestException;
import pl.ecookhub.api.repository.UserRepository;
import pl.ecookhub.api.service.mail.SendMailService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserActivationService {

    private final UserRepository userRepository;
    private final SendMailService sendMailService;

    //TODO create check for user is active or not active. If not active user dot have access to account.

    public String generateActivationKey() {
        return UUID.randomUUID().toString();
    }

    public boolean activateAccount(String activateKey) {
        User user = userRepository.findByActivation(activateKey);

        if (user == null) {
            throw new BadRequestException("Key is not valid");
        }

        user.setActivation("ACTIVE");
        userRepository.save(user);

        return true;
    }

    public void sendMailWithActivateAccountKey(String userEmail, String activateKey) {
        sendMailService.sendNoReplyEmail(
                userEmail,
                "ECookHub - Aktywacja konta",
                "Aby aktywować konto wklej podany adres w nowe okno przeglądarki: http://localhost:8080/users/activate?key=" + activateKey
        );
    }
}
