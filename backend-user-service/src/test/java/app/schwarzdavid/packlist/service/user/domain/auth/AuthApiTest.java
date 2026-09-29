package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.service.user.Application;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.CredentialsDto;
import app.schwarzdavid.packlist.service.user.fixture.UserDatabaseFixtures;
import app.schwarzdavid.packlist.service.user.fixture.UserFixtures;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Pool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ExtendWith({VertxExtension.class})
public class AuthApiTest {
    @Container
    private final static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine")
            .withDatabaseName("users-test")
            .withUsername("users-test")
            .withPassword("users-test");

    private Integer port;
    private Pool pool;

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
                .compose(verticle -> {
                    port = verticle.getPort();
                    pool = verticle.getPool();

                    return verticle.getPool().query("TRUNCATE TABLE users RESTART IDENTITY  CASCADE").execute();
                })
                .onComplete(ctx.succeeding(_ -> {
                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldLoginWithValidUsername(Vertx vertx, VertxTestContext ctx) {
        UserDatabaseFixtures.create(vertx, pool)
                .compose(user -> WebClient.create(vertx).post(port, "localhost", "/api/login")
                            .sendJsonObject(JsonObject.mapFrom(UserFixtures.credentialsWithUsernameIdentity()))
                            .onComplete(ctx.succeeding(response -> {
                                assertEquals(200, response.statusCode());

                                var body = response.bodyAsJsonObject();

                                assertEquals(user.userId(), body.getLong("userId"));
                                assertNotEquals(0, body.getString("token").length());
                                assertEquals(user.userId(), body.getJsonObject("user").getLong("userId"));
                                assertEquals(user.username(), body.getJsonObject("user").getString("username"));
                                assertEquals(user.email(), body.getJsonObject("user").getString("email"));
                                assertNull(body.getJsonObject("user").getString("password"));

                                ctx.completeNow();
                            })))
                .onFailure(ctx::failNow);
    }

    @Test
    public void shouldLoginWithValidEmail(Vertx vertx, VertxTestContext ctx) {
        UserDatabaseFixtures.create(vertx, pool)
                .compose(user -> WebClient.create(vertx).post(port, "localhost", "/api/login")
                        .sendJsonObject(JsonObject.mapFrom(UserFixtures.credentialsWithEmailIdentity()))
                        .onComplete(ctx.succeeding(response -> {
                            assertEquals(200, response.statusCode());

                            var body = response.bodyAsJsonObject();

                            assertEquals(user.userId(), body.getLong("userId"));
                            assertNotEquals(0, body.getString("token").length());
                            assertEquals(user.username(), body.getJsonObject("user").getString("username"));
                            assertEquals(user.email(), body.getJsonObject("user").getString("email"));
                            assertNull(body.getJsonObject("user").getString("password"));

                            ctx.completeNow();
                        })))
                .onFailure(ctx::failNow);
    }

    @Test
    public void shouldFailLoginWithInvalidIdentity(Vertx vertx, VertxTestContext ctx) {
        UserDatabaseFixtures.create(vertx, pool)
                .compose(_ -> WebClient.create(vertx).post(port, "localhost", "/api/login")
                        .sendJsonObject(JsonObject.mapFrom(new CredentialsDto("non-existing", UserFixtures.DEFAULT_PASSWORD))))
                .onComplete(ctx.succeeding(response -> {
                    assertEquals(401, response.statusCode());
                    assertTrue(response.bodyAsString().contains("Invalid Credentials"));

                    ctx.completeNow();
                }));

    }

    @Test
    public void shouldFailLoginWithInvalidPassword(Vertx vertx, VertxTestContext ctx) {
        UserDatabaseFixtures.create(vertx, pool)
                .compose(_ -> WebClient.create(vertx).post(port, "localhost", "/api/login")
                        .sendJsonObject(JsonObject.mapFrom(new CredentialsDto(UserFixtures.DEFAULT_EMAIL, "wront-password"))))
                .onComplete(ctx.succeeding(response -> {
                    assertEquals(401, response.statusCode());
                    assertTrue(response.bodyAsString().contains("Invalid Credentials"));

                    ctx.completeNow();
                }));
    }

    // TODO: Add test for malformed request body
    // TODO: Test for registration
}
