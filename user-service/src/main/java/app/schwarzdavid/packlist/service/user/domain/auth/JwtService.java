package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

public class JwtService {
    private final JWTAuth provider;

    public JwtService(Vertx vertx, Config config) {
        var secret = config.getValues().getString("JWT_SECRET");
        var pubSecKeyOptions = new PubSecKeyOptions().setAlgorithm("HS256").setBuffer(secret);
        var jwtOptions = new JWTOptions().setExpiresInMinutes(60 * 24 * 30);
        var jwtAuthOptions = new JWTAuthOptions()
                .addPubSecKey(pubSecKeyOptions)
                .setJWTOptions(jwtOptions);

        provider = JWTAuth.create(vertx, jwtAuthOptions);
    }

    public String generateTokenForUser(UserData userData) {
        JsonObject claims = new JsonObject();

        claims.put("sub", userData.userId());
        claims.put("email", userData.email());
        claims.put("nickname", userData.username());

        return provider.generateToken(claims);
    }
}
