package app.schwarzdavid.packlist.user.service.domain.auth;

import app.schwarzdavid.packlist.user.service.domain.user.UserService;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import app.schwarzdavid.packlist.user.service.fixture.AuthFixtures;
import app.schwarzdavid.packlist.user.service.fixture.UserFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void shouldRegisterUser() {
        when(passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD)).thenReturn(AuthFixtures.HASHED_PASSWORD);
        when(userService.createUser(assertArg(arg -> {
            assertEquals(UserFixtures.USERNAME, arg.username());
            assertEquals(UserFixtures.EMAIL, arg.email());
            assertEquals(AuthFixtures.HASHED_PASSWORD, arg.hashedPassword());
        }))).thenReturn(UserFixtures.userData());
        when(jwtService.generateToken(any(UserData.class))).thenReturn(AuthFixtures.JWT);

        var result = authService.registerUser(AuthFixtures.registrationData());

        verify(jwtService).generateToken(assertArg(arg -> {
            assertEquals(UserFixtures.USER_ID, arg.userId());
        }));

        assertEquals(UserFixtures.USER_ID, result.userId());
        assertEquals(AuthFixtures.JWT, result.token());
        assertInstanceOf(UserData.class, result.user());
        assertEquals(UserFixtures.USER_ID, result.user().userId());
        assertEquals(UserFixtures.USERNAME, result.user().username());
        assertEquals(UserFixtures.EMAIL, result.user().email());
    }
}
