package app.schwarzdavid.packlist.user.service.config;

import app.schwarzdavid.packlist.user.service.domain.user.security.UserCredentialsService;
import app.schwarzdavid.packlist.user.service.domain.user.security.UserPrincipal;
import app.schwarzdavid.packlist.user.service.fixture.AuthFixtures;
import app.schwarzdavid.packlist.user.service.fixture.UserFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = {"user-service.auth.password.strength=4"})
@SpringJUnitConfig({AuthConfig.class})
public class AuthConfigTest {
    @MockitoBean
    private UserCredentialsService userCredentialsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Nested
    class Authentication {
        @Test
        void shouldReturnAuthenticatedUser() {
            var passwordHash = passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD);
            var principal = new UserPrincipal(UserFixtures.USER_ID, UserFixtures.USERNAME, UserFixtures.EMAIL, passwordHash);
            when(userCredentialsService.loadUserByUsername(UserFixtures.USERNAME)).thenReturn(principal);

            var unauthenticatedAuthenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(UserFixtures.USERNAME, AuthFixtures.PLAIN_PASSWORD);
            var result = authenticationManager.authenticate(unauthenticatedAuthenticationToken);

            assertNull(result.getCredentials());
            assertInstanceOf(UserPrincipal.class, result.getPrincipal());
            assertTrue(result.isAuthenticated());
            var returnedPrincipal = (UserPrincipal) result.getPrincipal();
            assertNull(returnedPrincipal.getPassword());
            assertEquals(UserFixtures.USER_ID, returnedPrincipal.getUserId());
            assertEquals(UserFixtures.USERNAME, returnedPrincipal.getUsername());
            assertEquals(UserFixtures.EMAIL, returnedPrincipal.getEmail());
        }

        @Test
        void shouldThrowBadCredentialsExceptionWhenIdentifierIsWrong() {
            when(userCredentialsService.loadUserByUsername(UserFixtures.EMAIL)).thenThrow(new UsernameNotFoundException("Username not found"));

            var unauthenticatedAuthenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(UserFixtures.EMAIL, AuthFixtures.PLAIN_PASSWORD);
            assertThrows(BadCredentialsException.class, () -> authenticationManager.authenticate(unauthenticatedAuthenticationToken));
        }

        @Test
        void shouldThrowBadCredentialsExceptionWhenPasswordIsWrong() {
            var principal = new UserPrincipal(UserFixtures.USER_ID, UserFixtures.USERNAME, UserFixtures.EMAIL, UserFixtures.HASHED_PASSWORD);
            when(userCredentialsService.loadUserByUsername(UserFixtures.USERNAME)).thenReturn(principal);

            var unauthenticatedAuthenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(UserFixtures.USERNAME, AuthFixtures.PLAIN_PASSWORD);
            assertThrows(BadCredentialsException.class, () -> authenticationManager.authenticate(unauthenticatedAuthenticationToken));
        }
    }
}
