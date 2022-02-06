package pl.ecookhub.api.service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.ecookhub.api.entity.RefreshToken;
import pl.ecookhub.api.entity.User;
import pl.ecookhub.api.exception.refreshToken.ExpiredRefreshTokenException;
import pl.ecookhub.api.exception.refreshToken.InvalidRefreshTokenException;
import pl.ecookhub.api.exception.refreshToken.RefreshTokenHasAlreadyUsedException;
import pl.ecookhub.api.objectMother.UserBuilder;
import pl.ecookhub.api.repository.RefreshTokenRepository;
import pl.ecookhub.api.service.user.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RefreshTokenServiceTest {
    private RefreshTokenRepository mockRefreshTokenRepository;
    private UserService mockUserService;
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    public void setUp() {
        this.mockRefreshTokenRepository = mock(RefreshTokenRepository.class);
        this.mockUserService = mock(UserService.class);
        this.refreshTokenService = new RefreshTokenService(mockRefreshTokenRepository, mockUserService);
    }

    @Test
    void should_create_new_token() {
        // given
        User user = new User();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("ExampleRefreshToken");

        when(mockUserService.getUserById(anyLong())).thenReturn(user);
        when(mockRefreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // when
        String getNewRefreshTokenAfterSave = refreshTokenService.createNewRefreshToken(anyLong());

        // then
        assertEquals("ExampleRefreshToken", getNewRefreshTokenAfterSave);
    }

    @Test
    void should_change_isUsed_on_true_and_refresh_old_token() {
        // given
        User user = new User();

        RefreshToken oldRefreshToken = new RefreshToken();
        oldRefreshToken.setExpiryDate(LocalDateTime.now().plusMinutes(1));
        oldRefreshToken.setToken("ExampleOldRefreshToken");
        oldRefreshToken.setUsed(false);
        oldRefreshToken.setUser(user);

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken("ExampleNewRefreshToken");
        newRefreshToken.setUser(user);
        newRefreshToken.setUsed(false);
        newRefreshToken.setExpiryDate(LocalDateTime.now().plusMinutes(1));

        when(mockRefreshTokenRepository.findByToken(oldRefreshToken.getToken())).thenReturn(oldRefreshToken);
        when(mockRefreshTokenRepository.save(any(RefreshToken.class))).thenReturn(newRefreshToken);

        // when
        String getNewRefreshTokenAfterSave = refreshTokenService.refreshOldToken(oldRefreshToken.getToken());

        // then
        assertTrue(oldRefreshToken.isUsed());
        assertEquals("ExampleNewRefreshToken", getNewRefreshTokenAfterSave);
    }

    @Test
    void should_old_refresh_token_expire_exception() {
        // given
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        when(mockRefreshTokenRepository.findByToken("ExampleToken")).thenReturn(refreshToken);

        // when & then
        assertThatExceptionOfType(ExpiredRefreshTokenException.class)
                .isThrownBy(() -> refreshTokenService.refreshOldToken("ExampleToken"));
    }

    @Test
    void should_old_refresh_token_is_used_exception() {
        // given
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setExpiryDate(LocalDateTime.now().plusMinutes(1));
        refreshToken.setUsed(true);

        when(mockRefreshTokenRepository.findByToken("ExampleToken")).thenReturn(refreshToken);

        // when & then
        assertThatExceptionOfType(RefreshTokenHasAlreadyUsedException.class)
                .isThrownBy(() -> refreshTokenService.refreshOldToken("ExampleToken"));
    }

    @Test
    void should_find_user_by_refresh_token_invalid_exception() {
        // given
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(null);

        when(mockRefreshTokenRepository.findByToken("ExampleToken")).thenReturn(refreshToken);

        // when & then
        assertThatExceptionOfType(InvalidRefreshTokenException.class)
                .isThrownBy(() -> refreshTokenService.findUserByRefreshToken("ExampleToken"));
    }

    @Test
    void should_find_user_by_refresh_token() {
        // given
        User user = UserBuilder.newUser().build();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);

        when(mockRefreshTokenRepository.findByToken("ExampleToken")).thenReturn(refreshToken);

        // when
        User checkMethod = refreshTokenService.findUserByRefreshToken("ExampleToken");

        //then
        assertEquals(checkMethod.getUsername(), user.getUsername());
        assertThat(new User(), instanceOf(checkMethod.getClass()));
    }
}
