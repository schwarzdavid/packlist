package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.service.user.Application;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.CredentialsDto;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserDataWithPassword;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class AuthApiTest {
    @Container
    private final static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine")
            .withDatabaseName("users-test")
            .withUsername("users-test")
            .withPassword("users-test");

    private Integer port;

    @BeforeEach
    public void setup(Vertx vertx, VertxTestContext ctx) {
        Config
                .of(vertx, new JsonObject(Map.of(
                        "HTTP_PORT", 0,
                        "JWT_SECRET", "test",
                        "USER_POSTGRES_HOST", postgres.getHost(),
                        "USER_POSTGRES_PORT", postgres.getFirstMappedPort(),
                        "USER_POSTGRES_DB", postgres.getDatabaseName(),
                        "USER_POSTGRES_USER", postgres.getUsername(),
                        "USER_POSTGRES_PASSWORD", postgres.getPassword()
                )))
                .compose(config -> Application.start(vertx, config))
                .onComplete(ctx.succeeding(verticle -> {
                    port = verticle.getPort();
                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldLoginWithValidUsername(Vertx vertx, VertxTestContext ctx) {
        var user = new UserDataWithPassword(1L, "username", "email", "hashed-password");
        var credentials = new CredentialsDto("username", "plain-password");
        JsonObject body = JsonObject.mapFrom(credentials);

        WebClient.create(vertx).post(port, "localhost", "/api/login")
                .sendJsonObject(body)
                .onComplete(ctx.succeeding(response -> {
                    assertEquals(200, response.statusCode());
                    ctx.completeNow();
                }));
    }
}
