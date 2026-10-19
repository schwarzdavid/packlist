package app.schwarzdavid.packlist.common.security;

import org.assertj.core.api.Assertions;
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
        var authentication = UsernamePasswordAuthenticationToken.authenticated(new UserPrincipal(1L, "username", "test@example.com", "hashed-password"), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var userId = currentUserProvider.getCurrentUserId();

        Assertions.assertThat(userId).isEqualTo(1L);
    }

    @Test
    void shouldGetUserIdFromJwt() {
        var authentication = new JwtAuthenticationToken(Jwt.withTokenValue("token").header("alg", "none").subject("1").build());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var userId = currentUserProvider.getCurrentUserId();

        Assertions.assertThat(userId).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenNoAuthenticationExists() {
        Assertions.assertThatThrownBy(() -> currentUserProvider.getCurrentUserId())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessage("User not authenticated");
    }

    @Test
    void shouldThrowExceptionWhenPrincipalIsNull() {
        var authentication = new TestingAuthenticationToken(null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Assertions.assertThatThrownBy(() -> currentUserProvider.getCurrentUserId())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unknown principal type");
    }

    @Test
    void shouldThrowExceptionWhenPrincipalIsUnknownType() {
        var authentication = new TestingAuthenticationToken(new Object(), null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Assertions.assertThatThrownBy(() -> currentUserProvider.getCurrentUserId())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unknown principal type");
    }

    @Test
    void shouldThrowExceptionWhenJwtSubjectIsNull() {
        var authentication = new JwtAuthenticationToken(Jwt.withTokenValue("token").header("alg", "none").claim("foo", "bar").build());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Assertions.assertThatThrownBy(() -> currentUserProvider.getCurrentUserId())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT has no subject");
    }

    @Test
    void shouldThrowExceptionWhenJwtSubjectIsNotNumeric() {
        var authentication = new JwtAuthenticationToken(Jwt.withTokenValue("token").header("alg", "none").subject("abc").build());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Assertions.assertThatThrownBy(() -> currentUserProvider.getCurrentUserId())
                .isInstanceOf(NumberFormatException.class);
    }
}
