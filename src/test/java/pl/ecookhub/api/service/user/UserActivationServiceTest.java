package pl.ecookhub.api.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.ecookhub.api.entity.User;
import pl.ecookhub.api.exception.BadRequestException;
import pl.ecookhub.api.objectMother.UserBuilder;
import pl.ecookhub.api.repository.UserRepository;
import pl.ecookhub.api.service.mail.SendMailService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserActivationServiceTest {

    private UserRepository mockUserRepository;
    private SendMailService sendMailService;
    private UserActivationService userActivationService;

    @BeforeEach
    public void setUp() {
        this.mockUserRepository = mock(UserRepository.class);
        this.userActivationService = new UserActivationService(mockUserRepository, sendMailService);
    }

    @Test
    void should_active_user_account() {
        User user = UserBuilder.newUser().build();
        when(mockUserRepository.findByActivation(user.getActivation())).thenReturn(user);

        assertTrue(userActivationService.activateAccount(user.getActivation()));
    }

    @Test
    void should_active_user_account_has_bad_activation_key() {
        User user = UserBuilder.newUser().build();
        when(mockUserRepository.findByActivation(user.getActivation())).thenReturn(null);

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userActivationService.activateAccount(user.getActivation()));
    }
}