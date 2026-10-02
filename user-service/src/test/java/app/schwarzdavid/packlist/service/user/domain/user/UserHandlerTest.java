package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserCreateDto;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserDto;
import app.schwarzdavid.packlist.service.user.domain.user.exception.UserAlreadyExistsException;
import app.schwarzdavid.packlist.service.user.fixture.UserFixtures;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class UserHandlerTest {
    @Mock
    private UserService userService;

    @Mock
    private RoutingContext routingContext;

    @Mock
    private RequestBody requestBody;

    @Mock
    private HttpServerResponse response;

    @InjectMocks
    private UserHandler userHandler;

    @Test
    public void shouldCreateUser(VertxTestContext ctx) {
        when(routingContext.body()).thenReturn(requestBody);
        when(routingContext.response()).thenReturn(response);
        when(userService.createUser(any(UserCreateData.class))).thenReturn(Future.succeededFuture(UserFixtures.userData()));
        when(requestBody.asPojo(any())).thenReturn(new UserCreateDto(UserFixtures.USERNAME, UserFixtures.EMAIL, UserFixtures.PLAIN_PASSWORD));

        ctx.assertComplete(userHandler.createUser(routingContext))
                .onSuccess(userDto -> ctx.verify(() -> {
                    verify(response).setStatusCode(201);

                    assertNotNull(userDto);
                    assertInstanceOf(UserDto.class, userDto);
                    assertEquals(UserFixtures.ID, userDto.userId());
                    assertEquals(UserFixtures.USERNAME, userDto.username());
                    assertEquals(UserFixtures.EMAIL, userDto.email());

                    ctx.completeNow();
                }));
        ;
    }

    @Test
    public void shouldPassExceptionOnCreateUser(VertxTestContext ctx) {
        when(routingContext.body()).thenReturn(requestBody);
        when(requestBody.asPojo(any())).thenReturn(new UserCreateDto(UserFixtures.USERNAME, UserFixtures.EMAIL, UserFixtures.PLAIN_PASSWORD));
        when(userService.createUser(any(UserCreateData.class))).thenReturn(Future.failedFuture(new UserAlreadyExistsException(UserAlreadyExistsException.Field.EMAIL)));

        ctx.assertFailure(userHandler.createUser(routingContext))
                .onFailure(throwable -> ctx.verify(() -> {
                    assertInstanceOf(UserAlreadyExistsException.class, throwable);

                    ctx.completeNow();
                }));
    }
}
