package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.service.user.Application;
import app.schwarzdavid.packlist.service.user.domain.auth.JwtService;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserDataWithPassword;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserCreateDto;
import app.schwarzdavid.packlist.service.user.fixture.UserFixtures;
import app.schwarzdavid.packlist.service.user.service.security.PasswordHashingService;
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

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ExtendWith(VertxExtension.class)
public class UserApiTest {
    @Container
    private static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine")
            .withDatabaseName("users-test")
            .withUsername("users-test")
            .withPassword("users-test");

    private final static String CREATE_ROUTE = "/api/users";

    private Pool pool;
    private Integer port;
    private String jwt;

    @BeforeEach
    public void setup(Vertx vertx, VertxTestContext ctx) {
        JsonObject configValues = new JsonObject();

        configValues.put("HTTP_PORT", 0);
        configValues.put("JWT_SECRET", "secret");
        configValues.put("USER_POSTGRES_HOST", postgres.getHost());
        configValues.put("USER_POSTGRES_PORT", postgres.getFirstMappedPort());
        configValues.put("USER_POSTGRES_USER", postgres.getUsername());
        configValues.put("USER_POSTGRES_PASSWORD", postgres.getPassword());
        configValues.put("USER_POSTGRES_DB", postgres.getDatabaseName());

        Config
                .of(vertx, configValues)
                .onComplete(ctx.succeeding(config -> {
                    Application.start(vertx, config)
                            .compose(verticle -> {
                                port = verticle.getPort();
                                pool = verticle.getPool();

                                return pool.query("TRUNCATE TABLE users RESTART IDENTITY CASCADE").execute();
                            })
                            .compose(_ -> new PasswordHashingService(vertx).hashPassword("random-password"))
                            .compose(hashedPassword -> pool.preparedQuery("INSERT INTO users (username, email, password) VALUES ($1, $2, $3) RETURNING *")
                                    .execute(Tuple.of("test-user", "test@example.com", hashedPassword)))
                            .onComplete(ctx.succeeding(rows -> {
                                var user = rows.stream().findFirst().orElseThrow(() -> new RuntimeException("User could not be created.")).toJson().mapTo(UserData.class);
                                jwt = new JwtService(vertx, config).generateTokenForUser(user);

                                ctx.completeNow();
                            }));
                }));
    }

    @Test
    public void shouldCreateUser(Vertx vertx, VertxTestContext ctx) {
        var userCreateDto = new UserCreateDto(UserFixtures.USERNAME, UserFixtures.EMAIL, UserFixtures.PLAIN_PASSWORD);

        WebClient.create(vertx).post(port, "localhost", CREATE_ROUTE)
                .bearerTokenAuthentication(jwt)
                .sendJsonObject(JsonObject.mapFrom(userCreateDto))
                .onSuccess(response -> {
                    assertEquals(201, response.statusCode());

                    var body = response.bodyAsJsonObject();
                    assertNotNull(body.getLong("userId"));

                    pool.preparedQuery("SELECT * FROM users WHERE \"userId\" = $1").execute(Tuple.of(body.getLong("userId")))
                            .onComplete(ctx.succeeding(rows -> {
                                var userOptional = rows.stream().findFirst();

                                assertTrue(userOptional.isPresent());

                                var user = userOptional.get().toJson().mapTo(UserDataWithPassword.class);

                                assertEquals(user.userId(), body.getLong("userId"));
                                assertEquals(user.username(), body.getString("username"));
                                assertEquals(user.email(), body.getString("email"));
                                assertNull(body.getString("password"));

                                ctx.completeNow();
                            }));
                })
                .onFailure(ctx::failNow);
    }
}
