package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.common.JwtService;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserCreateDto;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.openapi.contract.OpenAPIContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith({MockitoExtension.class, VertxExtension.class})
public class UserRouterTest {
    private static final String CREATE_USER_PATH = "/api/users";

    @Mock
    private UserHandler userHandler;

    private Integer port;

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
}
