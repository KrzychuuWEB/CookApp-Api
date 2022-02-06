package pl.ecookhub.api.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.ecookhub.api.entity.User;
import pl.ecookhub.api.exception.BadRequestException;
import pl.ecookhub.api.repository.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserActivationService userActivationService;

    public Set<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new BadRequestException("User not found with id " + id));
    }

    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new BadRequestException("User not found with this email");
        }

        return user;
    }

    public String createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new BadRequestException("This email has been exists");
        }

        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new BadRequestException("This username has been exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String activateKey = userActivationService.generateActivationKey();
        user.setActivation(activateKey);

        userRepository.save(user);
        userActivationService.sendMailWithActivateAccountKey(user.getEmail(), activateKey);

        return user.getUsername();
    }

    public void updatePassword(User user, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("The current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }
}
