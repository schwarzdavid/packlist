package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.common.JwtService;
import app.schwarzdavid.packlist.common.data.UserData;
import app.schwarzdavid.packlist.service.user.domain.user.UserService;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.service.user.fixture.UserFixtures;
import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
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
public class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void shouldRegisterUser(VertxTestContext ctx) {
        var userCreateData = UserFixtures.create();
        var userData = UserFixtures.userData();

        when(userService.createUser(any(UserCreateData.class))).thenReturn(Future.succeededFuture(userData));
        when(jwtService.generateTokenForUser(any(UserData.class))).thenReturn("token");

        ctx.assertComplete(authService.registerUser(userCreateData))
                .onSuccess(auth -> ctx.verify(() -> {
                    verify(userService).createUser(assertArg(arg -> {
                        assertEquals(userCreateData, arg);
                    }));

                    verify(jwtService).generateTokenForUser(userData);

                    assertEquals(userData.userId(), auth.userId());
                    assertEquals("token", auth.token());
                    assertEquals(userData, auth.user());

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldNotGenerateTokenWhenUserServiceFails(VertxTestContext ctx) {
        when(userService.createUser(any(UserCreateData.class))).thenReturn(Future.failedFuture(new RuntimeException()));

        ctx.assertFailure(authService.registerUser(UserFixtures.create()))
                .onFailure(throwable -> ctx.verify(() -> {
                    assertInstanceOf(RuntimeException.class, throwable);
                    verifyNoInteractions(jwtService);

                    ctx.completeNow();
                }));
    }
}
