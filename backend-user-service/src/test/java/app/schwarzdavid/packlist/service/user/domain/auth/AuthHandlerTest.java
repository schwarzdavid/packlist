package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.service.user.domain.auth.data.AuthData;
import app.schwarzdavid.packlist.service.user.domain.auth.data.CredentialsData;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.AuthDto;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.CredentialsDto;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.RegistrationDto;
import app.schwarzdavid.packlist.service.user.domain.auth.exception.InvalidCredentialsException;
import app.schwarzdavid.packlist.service.user.domain.user.UserService;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserDto;
import app.schwarzdavid.packlist.service.user.domain.user.exception.UserAlreadyExistsException;
import app.schwarzdavid.packlist.service.user.fixture.UserFixtures;
import io.vertx.core.Future;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, VertxExtension.class})
public class AuthHandlerTest {
    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private CredentialsService credentialsService;

    @Mock
    private RoutingContext routingContext;

    @Mock
    private RequestBody requestBody;

    @InjectMocks
    private AuthHandler authHandler;

    @BeforeEach
    public void setUp() {
        when(routingContext.body()).thenReturn(requestBody);
    }

    @Test
    public void shouldLogin(VertxTestContext ctx) {
        when(requestBody.asPojo(any())).thenReturn(new CredentialsDto(UserFixtures.EMAIL, UserFixtures.PASSWORD));
        when(credentialsService.authenticate(any())).thenReturn(Future.succeededFuture(new AuthData(UserFixtures.ID, "token", UserFixtures.userData())));

        authHandler.login(routingContext)
                .onComplete(ctx.succeeding(authDto -> {
                    verify(credentialsService).authenticate(assertArg(credentials -> {
                        assertInstanceOf(CredentialsData.class, credentials);
                        assertEquals(UserFixtures.EMAIL, credentials.identifier());
                        assertEquals(UserFixtures.PASSWORD, credentials.password());
                    }));

                    assertInstanceOf(AuthDto.class, authDto);
                    assertEquals(UserFixtures.ID, authDto.userId());
                    assertEquals("token", authDto.token());
                    assertInstanceOf(UserDto.class, authDto.user());
                    assertEquals(UserFixtures.ID, authDto.user().userId());
                    assertEquals(UserFixtures.EMAIL, authDto.user().email());
                    assertEquals(UserFixtures.USERNAME, authDto.user().username());

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldFailFutureOnLoginIfErrorHappened(VertxTestContext ctx) {
        when(requestBody.asPojo(any())).thenReturn(new CredentialsDto(UserFixtures.USERNAME, UserFixtures.PASSWORD));
        when(credentialsService.authenticate(any())).thenReturn(Future.failedFuture(new InvalidCredentialsException()));

        ctx.assertFailure(authHandler.login(routingContext))
                .onComplete(ar -> ctx.completeNow());
    }

    @Test
    public void shouldRegister(VertxTestContext ctx) {
        when(requestBody.asPojo(any())).thenReturn(new RegistrationDto(UserFixtures.USERNAME, UserFixtures.EMAIL, UserFixtures.PASSWORD));
        when(userService.createUser(any())).thenReturn(Future.succeededFuture(UserFixtures.userData()));
        when(jwtService.generateTokenForUser(any())).thenReturn("token");

        ctx.assertComplete(authHandler.register(routingContext))
                .onSuccess(auth -> ctx.verify(() -> {
                    verify(userService).createUser(assertArg(userCreateData -> {
                        assertInstanceOf(UserCreateData.class, userCreateData);
                        assertEquals(UserFixtures.USERNAME, userCreateData.username());
                        assertEquals(UserFixtures.EMAIL, userCreateData.email());
                        assertEquals(UserFixtures.PASSWORD, userCreateData.password());
                    }));

                    assertInstanceOf(AuthDto.class, auth);
                    assertEquals(UserFixtures.ID, auth.userId());
                    assertEquals("token", auth.token());
                    assertInstanceOf(UserDto.class, auth.user());
                    assertEquals(UserFixtures.ID, auth.user().userId());
                    assertEquals(UserFixtures.USERNAME, auth.user().username());
                    assertEquals(UserFixtures.EMAIL, auth.user().email());

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldFailRegisterOnUserCreationError(VertxTestContext ctx) {
        when(requestBody.asPojo(any())).thenReturn(new RegistrationDto(UserFixtures.USERNAME, UserFixtures.EMAIL, UserFixtures.PASSWORD));
        when(userService.createUser(any())).thenReturn(Future.failedFuture(new UserAlreadyExistsException(UserAlreadyExistsException.Field.EMAIL)));

        ctx.assertFailure(authHandler.register(routingContext))
                .onFailure(throwable -> ctx.verify(() -> {
                    verify(jwtService, times(0)).generateTokenForUser(any());

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldFailRegisterOnJwtError(VertxTestContext ctx) {
        when(requestBody.asPojo(any())).thenReturn(new RegistrationDto(UserFixtures.USERNAME, UserFixtures.EMAIL, UserFixtures.PASSWORD));
        when(userService.createUser(any())).thenReturn(Future.succeededFuture(UserFixtures.userData()));
        when(jwtService.generateTokenForUser(any())).thenThrow(new RuntimeException());

        ctx.assertFailure(authHandler.register(routingContext))
                .onFailure(throwable -> ctx.completeNow());
    }
}
