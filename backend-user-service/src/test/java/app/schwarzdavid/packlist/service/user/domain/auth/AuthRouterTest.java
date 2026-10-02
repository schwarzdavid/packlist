package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.service.user.domain.auth.dto.AuthDto;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.CredentialsDto;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserDto;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.json.schema.Draft;
import io.vertx.json.schema.JsonSchemaOptions;
import io.vertx.json.schema.SchemaRepository;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class AuthRouterTest {
    private static final String LOGIN_PATH = "/login";
    @Mock
    private AuthHandler authHandler;
    private Integer port;

    static Stream<Arguments> validCredentials() {
        return Stream.of(
                Arguments.of(new CredentialsDto("username", "password")),
                Arguments.of(new CredentialsDto("test@example.com", "password")),
                Arguments.of(new CredentialsDto("\"👍🪦", "🤡"))
        );
    }

    static Stream<Arguments> invalidCredentials() {
        return Stream.of(
                Arguments.of(new CredentialsDto("", "")),
                Arguments.of(new CredentialsDto(null, "password")),
                Arguments.of(new CredentialsDto("username", null)),
                Arguments.of(new CredentialsDto("", "password")),
                Arguments.of(new CredentialsDto("username", ""))
        );
    }

    @BeforeEach
    public void setup(Vertx vertx, VertxTestContext ctx) {
        var schemaRepository = SchemaRepository.create(new JsonSchemaOptions().setDraft(Draft.DRAFT7).setBaseUri("https://schemas.packlist.app"));
        var authRouter = new AuthRouter(authHandler, schemaRepository);

        var router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().subRouter(authRouter.createRouter(vertx));

        vertx.createHttpServer().requestHandler(router).listen(0)
                .onComplete(ctx.succeeding(server -> {
                    port = server.actualPort();

                    ctx.completeNow();
                }));
    }

    @ParameterizedTest
    @MethodSource("validCredentials")
    public void shouldLoginValidCredentials(Object credentials, Vertx vertx, VertxTestContext ctx) {
        when(authHandler.login(any())).thenReturn(Future.succeededFuture(new AuthDto(1L, "token", new UserDto(1L, "user", "email"))));

        WebClient.create(vertx).post(port, "localhost", LOGIN_PATH)
                .sendJsonObject(JsonObject.mapFrom(credentials))
                .onComplete(ctx.succeeding(response -> {
                    assertEquals(200, response.statusCode());

                    ctx.completeNow();
                }));
    }

    @ParameterizedTest
    @MethodSource("invalidCredentials")
    public void shouldProhibitLoginWithMalformedCredentials(Object credentials, Vertx vertx, VertxTestContext ctx) {
        lenient().when(authHandler.login(any())).thenReturn(Future.succeededFuture(new AuthDto(1L, "token", new UserDto(1L, "user", "email"))));

        WebClient.create(vertx).post(port, "localhost", LOGIN_PATH)
                .sendJson(credentials)
                .onComplete(ctx.succeeding(response -> {
                    assertEquals(400, response.statusCode());
                    verifyNoInteractions(authHandler);

                    ctx.completeNow();
                }));
    }
}
