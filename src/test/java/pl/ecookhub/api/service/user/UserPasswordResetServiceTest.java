package pl.ecookhub.api.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.ecookhub.api.entity.ResetPassword;
import pl.ecookhub.api.entity.User;
import pl.ecookhub.api.exception.BadRequestException;
import pl.ecookhub.api.model.user.ResetPasswordModel;
import pl.ecookhub.api.objectMother.ResetPasswordBuilder;
import pl.ecookhub.api.objectMother.UserBuilder;
import pl.ecookhub.api.repository.ResetPasswordRepository;
import pl.ecookhub.api.service.mail.SendMailService;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

class UserPasswordResetServiceTest {

    private PasswordEncoder mockPasswordEncoder;
    private UserService mockUserService;
    private ResetPasswordRepository mockResetPasswordRepository;
    private UserPasswordResetService userPasswordResetService;

    @BeforeEach
    public void setUp() {
        SendMailService mockSendMailService = mock(SendMailService.class);
        this.mockPasswordEncoder = mock(PasswordEncoder.class);
        this.mockUserService = mock(UserService.class);
        this.mockResetPasswordRepository = mock(ResetPasswordRepository.class);
        this.userPasswordResetService = new UserPasswordResetService(mockSendMailService, mockPasswordEncoder, mockUserService, mockResetPasswordRepository);
    }

    @Test
    void should_delete_token_if_user_create_new_token() {
        User user = UserBuilder.newUser().build();
        ResetPassword resetPassword = ResetPasswordBuilder.newResetPassword().but().withUser(user).build();
        user.setResetPasswords(resetPassword);

        when(mockUserService.getUserByEmail(anyString())).thenReturn(user);

        userPasswordResetService.generateNewResetPasswordTokenAndSendMail("test@email.com");

        verify(mockResetPasswordRepository).deleteByUserId(user.getId());
    }

    @Test
    void should_create_new_reset_password_token() {
        User user = UserBuilder.newUser().build();

        when(mockUserService.getUserByEmail(anyString())).thenReturn(user);

        userPasswordResetService.generateNewResetPasswordTokenAndSendMail("test@email.com");

        verify(mockResetPasswordRepository).save(any(ResetPassword.class));
    }

    @Test
    void should_check_reset_token_is_valid_and_return_exception() {
        when(mockResetPasswordRepository.findByToken(anyInt())).thenReturn(null);

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userPasswordResetService.resetPassword(returnResetPasswordModel())).withMessageContaining("incorrect");
    }

    @Test
    void should_token_expired() {
        ResetPassword resetPassword = ResetPasswordBuilder.newResetPassword()
                .but()
                .withExpiryDate(LocalDateTime.now().minusDays(1))
                .build();

        when(mockResetPasswordRepository.findByToken(anyInt())).thenReturn(resetPassword);

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userPasswordResetService.resetPassword(returnResetPasswordModel())).withMessageContaining("expired");
    }

    @Test
    void should_return_exception_for_email_is_not_have_such_token() {
        ResetPassword resetPassword = ResetPasswordBuilder.newResetPassword()
                .but()
                .withUser(UserBuilder.newUser().withEmail("bad@email.com").build())
                .build();

        when(mockResetPasswordRepository.findByToken(anyInt())).thenReturn(resetPassword);

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userPasswordResetService.resetPassword(returnResetPasswordModel())).withMessageContaining("assigned");
    }

    @Test
    void should_change_password_and_delete_reset_token() {
        ResetPassword resetPassword = ResetPasswordBuilder.newResetPassword()
                .but()
                .withExpiryDate(LocalDateTime.now().plusDays(1))
                .build();

        when(mockResetPasswordRepository.findByToken(anyInt())).thenReturn(resetPassword);

        userPasswordResetService.resetPassword(returnResetPasswordModel());

        verify(mockPasswordEncoder).encode(anyString());
        verify(mockResetPasswordRepository).deleteByUserId(anyLong());
    }

    private ResetPasswordModel returnResetPasswordModel() {
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        resetPasswordModel.setPassword("test_password2");
        resetPasswordModel.setRepeatPassword("test_password2");
        resetPasswordModel.setToken(123456);
        resetPasswordModel.setEmail(UserBuilder.newUser().build().getEmail());

        return resetPasswordModel;
    }
}