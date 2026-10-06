package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.common.service.JwtService;
import app.schwarzdavid.packlist.service.user.domain.auth.exception.InvalidCredentialsException;
import app.schwarzdavid.packlist.service.user.domain.user.UserService;
import app.schwarzdavid.packlist.service.user.fixture.UserFixtures;
import app.schwarzdavid.packlist.service.user.infrastructure.security.PasswordHashingService;
import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, VertxExtension.class})
public class CredentialsServiceTest {
    @Mock
    private JwtService jwtServiceMock;
    @Mock
    private UserService userServiceMock;
    @Mock
    private PasswordHashingService passwordHashingServiceMock;
    @InjectMocks
    private CredentialsService credentialsService;

    @Test
    public void authenticateCorrectCredentials(VertxTestContext ctx) {
        var user = UserFixtures.withPassword();
        var credentials = UserFixtures.credentialsWithEmailIdentity();

        when(userServiceMock.getUserDataWithPasswordByIdentifier(anyString())).thenReturn(Future.succeededFuture(Optional.of(user)));
        when(passwordHashingServiceMock.verifyPassword(anyString(), anyString())).thenReturn(Future.succeededFuture(true));
        when(jwtServiceMock.generateTokenForUser(any())).thenReturn("token");

        ctx.assertComplete(credentialsService.authenticate(credentials))
                .onSuccess(auth -> ctx.verify(() -> {
                    verify(userServiceMock).getUserDataWithPasswordByIdentifier(credentials.identifier());
                    verify(passwordHashingServiceMock).verifyPassword(UserFixtures.HASHED_PASSWORD, credentials.password());
                    verify(jwtServiceMock).generateTokenForUser(any());

                    assertEquals("token", auth.token());
                    assertEquals(UserFixtures.ID, auth.userId());
                    assertEquals(UserFixtures.ID, auth.user().userId());
                    assertEquals(UserFixtures.USERNAME, auth.user().username());
                    assertEquals(UserFixtures.EMAIL, auth.user().email());

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldThrowIfUserIsNotFound(VertxTestContext ctx) {
        var credentials = UserFixtures.credentialsWithEmailIdentity();

        when(userServiceMock.getUserDataWithPasswordByIdentifier(anyString())).thenReturn(Future.succeededFuture(Optional.empty()));

        credentialsService.authenticate(credentials)
                .onComplete(ctx.failing(exception -> {
                    verify(userServiceMock).getUserDataWithPasswordByIdentifier(credentials.identifier());
                    verifyNoInteractions(passwordHashingServiceMock);
                    verifyNoInteractions(jwtServiceMock);

                    assertInstanceOf(InvalidCredentialsException.class, exception);
                    assertEquals(401, ((InvalidCredentialsException) exception).getStatusCode());

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldThrowIfPasswordIsWrong(VertxTestContext ctx) {
        var user = UserFixtures.withPassword();
        var credentials = UserFixtures.credentialsWithEmailIdentity();

        when(userServiceMock.getUserDataWithPasswordByIdentifier(anyString())).thenReturn(Future.succeededFuture(Optional.of(user)));
        when(passwordHashingServiceMock.verifyPassword(anyString(), anyString())).thenReturn(Future.succeededFuture(false));

        credentialsService.authenticate(credentials)
                .onComplete(ctx.failing(exception -> {
                    verify(userServiceMock).getUserDataWithPasswordByIdentifier(UserFixtures.EMAIL);
                    verify(passwordHashingServiceMock).verifyPassword(UserFixtures.HASHED_PASSWORD, UserFixtures.PLAIN_PASSWORD);
                    verifyNoInteractions(jwtServiceMock);

                    assertInstanceOf(InvalidCredentialsException.class, exception);
                    assertEquals(401, ((InvalidCredentialsException) exception).getStatusCode());

                    ctx.completeNow();
                }));
    }
}
