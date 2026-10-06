package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.common.infrastructure.Config;
import app.schwarzdavid.packlist.common.service.JwtService;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserCreateDto;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserDto;
import app.schwarzdavid.packlist.service.user.fixture.UserFixtures;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.openapi.contract.OpenAPIContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, VertxExtension.class})
public class UserRouterTest {
    private static final String CREATE_USER_PATH = "/api/users";

    private String token;

    static Stream<Arguments> validUserCreation() {
        return Stream.of(
                Arguments.of(new UserCreateDto("username", "email@example.com", "password")),
                Arguments.of(new UserCreateDto(" username ", "email@example.com", "password")),
                Arguments.of(new UserCreateDto("a", "a@a.at", "password")),
                Arguments.of(new UserCreateDto("%&/()", "email@example.com", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
        );
    }

    @Mock
    private UserHandler userHandler;

    private Integer port;

    static Stream<Arguments> invalidUserCreation() {
        return Stream.of(
                Arguments.of(new UserCreateDto("", "email@example.com", "password")),
                Arguments.of(new UserCreateDto("username", "no-email", "password")),
                Arguments.of(new UserCreateDto(null, "email@example.com", "password")),
                Arguments.of(new UserCreateDto("username", null, "password")),
                Arguments.of(new UserCreateDto("username", "email@example.com", "short")),
                Arguments.of(new UserCreateDto("username", "email@example.com", null)),
                Arguments.of(JsonObject.mapFrom(new UserCreateDto("username", "email@example.com", "password")).put("passwordRepeat", "password"))
        );
    }

    @BeforeEach
    public void setup(Vertx vertx, VertxTestContext ctx) {
        var configFuture = Config.of(vertx, JsonObject.of("JWT_SECRET", "secret"));
        var contractFuture = OpenAPIContract.from(vertx, "users-api.yml");

        Future.all(configFuture, contractFuture)
                .compose(futures -> {
                    var config = futures.<Config>resultAt(0);
                    var contract = futures.<OpenAPIContract>resultAt(1);

                    var jwtService = new JwtService(vertx, config);
                    var userRouter = new UserRouter(userHandler, contract, jwtService);

                    token = jwtService.generateTokenForUser(UserFixtures.userData());

                    var router = Router.router(vertx);
                    router.route()
                            .handler(BodyHandler.create())
                            .subRouter(userRouter.createRouter(vertx));

                    return vertx.createHttpServer().requestHandler(router).listen(0);
                })
                .onComplete(ctx.succeeding((server) -> {
                    port = server.actualPort();

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldNotCreateUserWhenNotLoggedIn(Vertx vertx, VertxTestContext ctx) {
        var userCreateDto = new UserCreateDto("username", "email@example.com", "password");
        WebClient.create(vertx).post(port, "localhost", CREATE_USER_PATH)
                .sendJson(userCreateDto)
                .onComplete(ctx.succeeding(response -> {
                    assertEquals(401, response.statusCode());
                    verifyNoInteractions(userHandler);

                    ctx.completeNow();
                }));
    }

    @ParameterizedTest
    @MethodSource("validUserCreation")
    public void shouldCreateValidUsersWhenLoggedIn(UserCreateDto userCreateDto, Vertx vertx, VertxTestContext ctx) {
        when(userHandler.createUser(any(RoutingContext.class))).thenAnswer(invocation -> {
            var routingContext = invocation.getArgument(0, RoutingContext.class);
            var body = routingContext.body().asJsonObject();
            routingContext.response().setStatusCode(201);
            return Future.succeededFuture(new UserDto(2L, body.getString("username"), body.getString("email")));
        });

        WebClient.create(vertx).post(port, "localhost", CREATE_USER_PATH)
                .bearerTokenAuthentication(token)
                .sendJson(userCreateDto)
                .onComplete(ctx.succeeding(response -> {
                    assertEquals(201, response.statusCode());
                    assertEquals(2L, response.bodyAsJsonObject().getLong("userId"));
                    assertEquals(userCreateDto.username(), response.bodyAsJsonObject().getString("username"));
                    assertEquals(userCreateDto.email(), response.bodyAsJsonObject().getString("email"));

                    ctx.completeNow();
                }));
    }

    @ParameterizedTest
    @MethodSource("invalidUserCreation")
    public void shouldNotCreateInvalidUsers(Object user, Vertx vertx, VertxTestContext ctx) {
        WebClient.create(vertx).post(port, "localhost", CREATE_USER_PATH)
                .bearerTokenAuthentication(token)
                .sendJson(user)
                .onComplete(ctx.succeeding(response -> {
                    assertEquals(400, response.statusCode());
                    verifyNoInteractions(userHandler);

                    ctx.completeNow();
                }));
    }
}
