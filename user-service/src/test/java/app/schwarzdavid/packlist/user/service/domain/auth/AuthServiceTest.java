package app.schwarzdavid.packlist.user.service.domain.auth;

import app.schwarzdavid.packlist.user.service.domain.auth.exception.InvalidCredentialsException;
import app.schwarzdavid.packlist.user.service.domain.user.UserService;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import app.schwarzdavid.packlist.user.service.domain.user.exception.UserNotFoundException;
import app.schwarzdavid.packlist.user.service.fixture.AuthFixtures;
import app.schwarzdavid.packlist.user.service.fixture.UserFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Nested
    class Register {
        @Test
        void shouldRegisterUser() {
            when(passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD)).thenReturn(UserFixtures.HASHED_PASSWORD);
            when(userService.createUser(UserFixtures.userCreateData())).thenReturn(UserFixtures.userData());
            when(jwtService.generateToken(any(UserData.class))).thenReturn(AuthFixtures.JWT);

            var result = authService.registerUser(AuthFixtures.registrationData());

            verify(jwtService).generateToken(assertArg(arg -> {
                assertEquals(UserFixtures.userData(), arg);
            }));

            assertEquals(UserFixtures.USER_ID, result.userId());
            assertEquals(AuthFixtures.JWT, result.token());
            assertEquals(UserFixtures.userData(), result.user());
        }

        @Test
        void shouldAbortRegistrationIfUserAlreadyExists() {
            when(passwordEncoder.encode(anyString())).thenReturn(UserFixtures.HASHED_PASSWORD);
            when(userService.createUser(any(UserCreateData.class))).thenThrow(new RuntimeException());

            assertThrows(RuntimeException.class, () -> authService.registerUser(AuthFixtures.registrationData()));
            verifyNoInteractions(jwtService);
        }
    }

    @Nested
    class Login {
        @Test
        void shouldLoginUserWithValidCredentials() {
            when(userService.getUserByIdentifier(AuthFixtures.IDENTIFIER)).thenReturn(UserFixtures.userDataWithPassword());
            when(passwordEncoder.matches(AuthFixtures.PLAIN_PASSWORD, UserFixtures.HASHED_PASSWORD)).thenReturn(true);
            when(jwtService.generateToken(UserFixtures.userData())).thenReturn(AuthFixtures.JWT);

            var result = authService.loginUser(AuthFixtures.credentialsData());

            assertEquals(UserFixtures.USER_ID, result.userId());
            assertEquals(AuthFixtures.JWT, result.token());
            assertEquals(UserFixtures.userData(), result.user());
        }

        @Test
        void shouldThrowInvalidCredentialsExceptionWhenUserNotFound() {
            when(userService.getUserByIdentifier(AuthFixtures.IDENTIFIER)).thenThrow(new UserNotFoundException());

            assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(AuthFixtures.credentialsData()));

            verifyNoInteractions(jwtService);
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        void shouldThrowInvalidCredentialsExceptionWhenPasswordIsIncorrect() {
            when(userService.getUserByIdentifier(AuthFixtures.IDENTIFIER)).thenReturn(UserFixtures.userDataWithPassword());
            when(passwordEncoder.matches(AuthFixtures.PLAIN_PASSWORD, UserFixtures.HASHED_PASSWORD)).thenReturn(false);

            assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(AuthFixtures.credentialsData()));

            verifyNoInteractions(jwtService);
        }
    }
}
