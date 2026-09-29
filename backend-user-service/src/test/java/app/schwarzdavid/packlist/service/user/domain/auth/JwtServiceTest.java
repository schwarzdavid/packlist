package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, VertxExtension.class})
public class JwtServiceTest {
    private static final String JWT_SECRET = "secret";
    private static final Long JWT_DEFAULT_LIFETIME_MINUTES = 60 * 24 * 30L;
    private static final Long JWT_DEFAULT_LIFETIME_SECONDS = JWT_DEFAULT_LIFETIME_MINUTES * 60L;

    @Mock
    private Config config;

    private static JsonObject getConfigValues() {
        return getConfigValues(null);
    }

    private static JsonObject getConfigValues(Integer ttl) {
        var config = new JsonObject();

        config.put("JWT_SECRET", JWT_SECRET);
        if (ttl != null) {
            config.put("JWT_TTL_MINUTES", ttl);
        }

        return config;
    }

    private static JWTAuth getVerifier(Vertx vertx) {
        return getVerifier(vertx, JWT_SECRET);
    }

    private static JWTAuth getVerifier(Vertx vertx, String secret) {
        return JWTAuth.create(vertx, new JWTAuthOptions().addPubSecKey(new PubSecKeyOptions().setAlgorithm("HS256").setBuffer(secret)));
    }

    @Test
    public void shouldGenerateTokenWithDefaultLifetime(Vertx vertx, VertxTestContext ctx) {
        when(config.getValues()).thenReturn(getConfigValues());

        var jwtService = new JwtService(vertx, config);
        var verifier = getVerifier(vertx);
        var user = new UserData(1L, "username", "email@example.com");
        var token = jwtService.generateTokenForUser(user);

        verifier.authenticate(new TokenCredentials(token))
                .onComplete(ctx.succeeding(tokenUser -> {
                    assertEquals("1", tokenUser.subject());
                    assertEquals("email@example.com", tokenUser.principal().getString("email"));
                    assertEquals("username", tokenUser.principal().getString("nickname"));
                    assertEquals(JWT_DEFAULT_LIFETIME_SECONDS, tokenUser.attributes().getInteger("exp") - tokenUser.attributes().getInteger("iat"));

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldGenerateTokenWithCustomLifetime(Vertx vertx, VertxTestContext ctx) {
        when(config.getValues()).thenReturn(getConfigValues(100));

        var jwtService = new JwtService(vertx, config);
        var verifier = getVerifier(vertx);
        var user = new UserData(1L, "username", "email");
        var token = jwtService.generateTokenForUser(user);

        verifier.authenticate(new TokenCredentials(token))
                .onComplete(ctx.succeeding(tokenUser -> {
                    assertEquals(100 * 60, tokenUser.attributes().getInteger("exp") - tokenUser.attributes().getInteger("iat"));
                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldGenerateAuthHandler(Vertx vertx) {
        when(config.getValues()).thenReturn(getConfigValues());

        var jwtService = new JwtService(vertx, config);

        assertNotNull(jwtService.getHandler());
        assertInstanceOf(JWTAuthHandler.class, jwtService.getHandler());
    }
}
