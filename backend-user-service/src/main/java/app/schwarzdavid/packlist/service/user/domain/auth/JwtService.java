package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.handler.JWTAuthHandler;
import lombok.Getter;

public class JwtService {
    private final JWTAuth provider;
    @Getter private final JWTAuthHandler handler;

    public JwtService(Vertx vertx, Config config) {
        var secret = config.getValues().getString("JWT_SECRET");
        var tokenTTL = config.getValues().getInteger("JWT_TTL_MINUTES", 60 * 24 * 30);

        var pubSecKeyOptions = new PubSecKeyOptions().setAlgorithm("HS256").setBuffer(secret);
        var jwtOptions = new JWTOptions().setExpiresInMinutes(tokenTTL);
        var jwtAuthOptions = new JWTAuthOptions()
                .addPubSecKey(pubSecKeyOptions)
                .setJWTOptions(jwtOptions);

        provider = JWTAuth.create(vertx, jwtAuthOptions);
        handler = JWTAuthHandler.create(provider);
    }

    public String generateTokenForUser(UserData userData) {
        JsonObject claims = new JsonObject();

        claims.put("sub", userData.userId().toString());
        claims.put("email", userData.email());
        claims.put("nickname", userData.username());

        return provider.generateToken(claims);
    }
}
