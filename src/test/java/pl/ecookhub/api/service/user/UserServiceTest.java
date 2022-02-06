package pl.ecookhub.api.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.ecookhub.api.entity.User;
import pl.ecookhub.api.exception.BadRequestException;
import pl.ecookhub.api.objectMother.UserBuilder;
import pl.ecookhub.api.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private UserRepository mockUserRepository;
    private PasswordEncoder mockPasswordEncoder;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        this.mockUserRepository = mock(UserRepository.class);
        this.mockPasswordEncoder = mock(PasswordEncoder.class);
        UserActivationService userActivationService = mock(UserActivationService.class);
        this.userService = new UserService(mockUserRepository, mockPasswordEncoder, userActivationService);
    }

    @Test
    void should_return_all_users() {
        // given
        Set<User> users = UserBuilder.newUser().usersSetList();
        when(mockUserRepository.getAllUsers()).thenReturn(users);

        // when
        Set<User> returnUsers = userService.getAllUsers();

        // then
        assertEquals(2, returnUsers.size());
    }

    @Test
    void should_return_user_by_id() {
        // given
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(UserBuilder.newUser().build()));

        // when && then
        assertThat(new User(), instanceOf(userService.getUserById(1).getClass()));
        assertNotNull(userService.getUserById(1).getUsername());
    }

    @Test
    void should_not_return_user_expected_exception() {
        // given
        when(mockUserRepository.findById(null)).thenReturn(null);

        // when && then
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.getUserById(0));
    }

    @Test
    void should_create_new_user() {
        // given
        User user = UserBuilder.newUser().build();
        when(mockUserRepository.findByEmail(anyString())).thenReturn(null);
        when(mockUserRepository.findByUsername(anyString())).thenReturn(null);
        when(mockPasswordEncoder.encode(anyString())).thenReturn("hashPassword");

        when(mockUserRepository.save(UserBuilder.newUser().build())).thenReturn(user);

        // when && then
        assertEquals("test_username", userService.createUser(user));
    }

    @Test
    void should_email_has_been_used() {
        // given
        when(mockUserRepository.findByEmail(anyString())).thenReturn(UserBuilder.newUser().build());

        // when && then
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.createUser(UserBuilder.newUser().build()));
    }

    @Test
    void should_username_has_been_used() {
        // given
        when(mockUserRepository.findByEmail(anyString())).thenReturn(null);
        when(mockUserRepository.findByUsername(anyString())).thenReturn(UserBuilder.newUser().build());

        // when && then
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.createUser(UserBuilder.newUser().build()));
    }

    @Test
    void should_not_found_user_with_email() {
        when(mockUserRepository.findByEmail(anyString())).thenReturn(null);

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.getUserByEmail(UserBuilder.newUser().build().getEmail()));
    }

    @Test
    void should_found_user_with_email() {
        User userMother = UserBuilder.newUser().build();
        when(mockUserRepository.findByEmail(anyString())).thenReturn(userMother);

        User userFromUserService = userService.getUserByEmail(userMother.getEmail());

        assertThat(new User(), instanceOf( userFromUserService.getClass()));
        assertEquals(userMother.getUsername(), userFromUserService.getUsername());
    }

    @Test
    void should_current_password_is_incorrect_bad_request_exception() {
        when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> userService.updatePassword(
                                UserBuilder.newUser().build(),
                                "bad_current_password",
                                "new_password"
                        )
                );
    }
}
