package app.schwarzdavid.packlist.user.service.domain.auth;

import app.schwarzdavid.packlist.user.service.domain.user.UserService;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import app.schwarzdavid.packlist.user.service.domain.user.exception.UserAlreadyExistsException;
import app.schwarzdavid.packlist.user.service.fixture.AuthFixtures;
import app.schwarzdavid.packlist.user.service.fixture.UserFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private AuthenticationManager authenticationManager;

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
            when(userService.createUser(any(UserCreateData.class))).thenThrow(new UserAlreadyExistsException(UserAlreadyExistsException.Field.EMAIL));

            assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(AuthFixtures.registrationData()));
            verifyNoInteractions(jwtService);
        }
    }

    @Nested
    class Login {
        @Test
        void shouldLoginUserWithValidCredentials() {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(AuthFixtures.authentication());
            when(jwtService.generateToken(UserFixtures.userData())).thenReturn(AuthFixtures.JWT);

            var credentials = AuthFixtures.credentialsData();
            var result = authService.loginUser(credentials);

            var unauthenticatedAuthenticationCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            verify(authenticationManager).authenticate(unauthenticatedAuthenticationCaptor.capture());
            var unauthenticatedAuthentication = unauthenticatedAuthenticationCaptor.getValue();

            assertFalse(unauthenticatedAuthentication.isAuthenticated());
            assertEquals(credentials.identifier(), unauthenticatedAuthentication.getPrincipal());
            assertEquals(credentials.password(), unauthenticatedAuthentication.getCredentials());

            verify(jwtService).generateToken(UserFixtures.userData());

            assertEquals(UserFixtures.USER_ID, result.userId());
            assertEquals(AuthFixtures.JWT, result.token());
            assertEquals(UserFixtures.userData(), result.user());
        }

        @Test
        void shouldThrowWhenAuthenticationNotPossible() {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Wrong credentials"));

            assertThrows(BadCredentialsException.class, () -> authService.loginUser(AuthFixtures.credentialsData()));

            verifyNoInteractions(jwtService);
        }

        @Test
        void shouldThrowWhenPrincipalIsNotPresent() {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(UsernamePasswordAuthenticationToken.authenticated(null, null, List.of()));

            assertThrows(BadCredentialsException.class, () -> authService.loginUser(AuthFixtures.credentialsData()));

            verifyNoInteractions(jwtService);
        }
    }
}
