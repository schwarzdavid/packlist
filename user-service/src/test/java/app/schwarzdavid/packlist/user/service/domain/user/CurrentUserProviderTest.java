package app.schwarzdavid.packlist.user.service.domain.user;

import app.schwarzdavid.packlist.user.service.domain.user.security.UserPrincipal;
import app.schwarzdavid.packlist.user.service.fixture.UserFixtures;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CurrentUserProviderTest {
    private CurrentUserProvider currentUserProvider;

    @BeforeEach
    void setup() {
        currentUserProvider = new CurrentUserProvider();
    }

    @AfterEach
    void teardown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldGetUserIdFromUserPrincipal() {
        var authentication = UsernamePasswordAuthenticationToken.authenticated(new UserPrincipal(UserFixtures.USER_ID, UserFixtures.USERNAME, UserFixtures.EMAIL, UserFixtures.HASHED_PASSWORD), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var userId = currentUserProvider.getCurrentUserId();

        assertThat(userId).isEqualTo(UserFixtures.USER_ID);
    }

    @Test
    void shouldGetUserIdFromJwt() {
        var authentication = new JwtAuthenticationToken(Jwt.withTokenValue("token").header("alg", "none").subject(UserFixtures.USER_ID.toString()).build());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var userId = currentUserProvider.getCurrentUserId();

        assertThat(userId).isEqualTo(UserFixtures.USER_ID);
    }

    @Test
    void shouldThrowExceptionWhenNoAuthenticationExists() {
        assertThatThrownBy(() -> currentUserProvider.getCurrentUserId())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessage("User not authenticated");
    }

    @Test
    void shouldThrowExceptionWhenPrincipalIsNull() {
        var authentication = new TestingAuthenticationToken(null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatThrownBy(() -> currentUserProvider.getCurrentUserId())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unknown principal type");
    }

    @Test
    void shouldThrowExceptionWhenPrincipalIsUnknownType() {
        var authentication = new TestingAuthenticationToken(new Object(), null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatThrownBy(() -> currentUserProvider.getCurrentUserId())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unknown principal type");
    }

    @Test
    void shouldThrowExceptionWhenJwtSubjectIsNull() {
        var authentication = new JwtAuthenticationToken(Jwt.withTokenValue("token").header("alg", "none").claim("foo", "bar").build());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatThrownBy(() -> currentUserProvider.getCurrentUserId())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT has no subject");
    }

    @Test
    void shouldThrowExceptionWhenJwtSubjectIsNotNumeric() {
        var authentication = new JwtAuthenticationToken(Jwt.withTokenValue("token").header("alg", "none").subject("abc").build());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatThrownBy(() -> currentUserProvider.getCurrentUserId())
                .isInstanceOf(NumberFormatException.class);
    }
}
