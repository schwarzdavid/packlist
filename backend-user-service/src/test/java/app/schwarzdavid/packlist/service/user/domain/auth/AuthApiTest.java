package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.service.user.Application;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.CredentialsDto;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.RegistrationDto;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserDataWithPassword;
import app.schwarzdavid.packlist.service.user.fixture.UserDatabaseFixtures;
import app.schwarzdavid.packlist.service.user.fixture.UserFixtures;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;
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

    private final static String LOGIN_ROUTE = "/api/login";
    private final static String REGISTER_ROUTE = "/api/register";

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
                .onComplete(ctx.succeeding(_ -> ctx.completeNow()));
    }

    @Test
    public void shouldLoginWithValidUsername(Vertx vertx, VertxTestContext ctx) {
        UserDatabaseFixtures.create(vertx, pool)
                .compose(user -> WebClient.create(vertx).post(port, "localhost", LOGIN_ROUTE)
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
                .compose(user -> WebClient.create(vertx).post(port, "localhost", LOGIN_ROUTE)
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
                .compose(_ -> WebClient.create(vertx).post(port, "localhost", LOGIN_ROUTE)
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
                .compose(_ -> WebClient.create(vertx).post(port, "localhost", LOGIN_ROUTE)
                        .sendJsonObject(JsonObject.mapFrom(new CredentialsDto(UserFixtures.DEFAULT_EMAIL, "wrong-password"))))
                .onComplete(ctx.succeeding(response -> {
                    assertEquals(401, response.statusCode());
                    assertTrue(response.bodyAsString().contains("Invalid Credentials"));

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldRegisterNewUser(Vertx vertx, VertxTestContext ctx) {
        WebClient.create(vertx).post(port, "localhost", REGISTER_ROUTE)
                .sendJsonObject(JsonObject.mapFrom(UserFixtures.create()))
                .onComplete(ctx.succeeding(response -> {
                    assertEquals(201, response.statusCode());

                    var body = response.bodyAsJsonObject();

                    assertNotNull(body.getLong("userId"));
                    assertNotEquals(0, body.getString("token").length());
                    assertNotNull(body.getJsonObject("user"));
                    assertNotNull(body.getJsonObject("user").getLong("userId"));
                    assertEquals(UserFixtures.DEFAULT_USERNAME, body.getJsonObject("user").getString("username"));
                    assertEquals(UserFixtures.DEFAULT_EMAIL, body.getJsonObject("user").getString("email"));
                    assertNull(body.getJsonObject("user").getString("password"));

                    pool.preparedQuery("SELECT * FROM users WHERE \"userId\" = $1").execute(Tuple.of(body.getLong("userId")))
                            .map(rowSet -> rowSet.stream().findFirst().map(row -> row.toJson().mapTo(UserDataWithPassword.class)))
                            .onComplete(ctx.succeeding(userOptional -> {
                                assertTrue(userOptional.isPresent());

                                var user = userOptional.get();

                                assertEquals(UserFixtures.DEFAULT_USERNAME, user.username());
                                assertEquals(UserFixtures.DEFAULT_EMAIL, user.email());
                                assertNotEquals(UserFixtures.DEFAULT_PASSWORD, user.password());

                                ctx.completeNow();
                            }));
                }));
    }

    @Test
    public void shouldNotRegisterIfEmailAlreadyExists(Vertx vertx, VertxTestContext ctx) {
        UserDatabaseFixtures.create(vertx, pool)
                .compose(user -> WebClient.create(vertx).post(port, "localhost", REGISTER_ROUTE)
                        .sendJsonObject(JsonObject.mapFrom(new RegistrationDto("other-username", UserFixtures.DEFAULT_EMAIL, UserFixtures.DEFAULT_PASSWORD))))
                .onComplete(ctx.succeeding(response -> {
                    assertEquals(409, response.statusCode());
                    assertTrue(response.bodyAsString().contains("e-mail"));

                    pool.preparedQuery("SELECT * FROM users WHERE email = $1")
                            .execute(Tuple.of(UserFixtures.DEFAULT_EMAIL))
                            .onSuccess(rowSet -> {
                                assertEquals(1, rowSet.size());

                                var user = rowSet.iterator().next().toJson();

                                assertEquals(UserFixtures.DEFAULT_USERNAME, user.getString("username"));

                                ctx.completeNow();
                            })
                            .onFailure(ctx::failNow);
                }));
    }

    @Test
    public void shouldNotRegisterIfUsernameAlreadyExists(Vertx vertx, VertxTestContext ctx) {
        UserDatabaseFixtures.create(vertx, pool)
                .compose(user -> WebClient.create(vertx).post(port, "localhost", REGISTER_ROUTE)
                        .sendJsonObject(JsonObject.mapFrom(new RegistrationDto(UserFixtures.DEFAULT_USERNAME, "other@email.address", UserFixtures.DEFAULT_PASSWORD))))
                .onComplete(ctx.succeeding(response -> {
                    assertEquals(409, response.statusCode());
                    assertTrue(response.bodyAsString().contains("username"));

                    pool.preparedQuery("SELECT * FROM users WHERE username = $1")
                            .execute(Tuple.of(UserFixtures.DEFAULT_USERNAME))
                            .onSuccess(rowSet -> {
                                assertEquals(1, rowSet.size());

                                var user = rowSet.iterator().next().toJson();

                                assertEquals(UserFixtures.DEFAULT_USERNAME, user.getString("username"));

                                ctx.completeNow();
                            })
                            .onFailure(ctx::failNow);
                }));
    }

    // TODO: Add test for malformed request body (email is not email, password too short, ...)
}
