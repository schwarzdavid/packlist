package app.schwarzdavid.packlist.service.user.infrastructure.security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.LongPasswordStrategies;
import at.favre.lib.crypto.bcrypt.LongPasswordStrategy;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.security.SecureRandom;

public class PasswordHashingService {
    private static final BCrypt.Version VERSION = BCrypt.Version.VERSION_2Y;
    private static final LongPasswordStrategy STRATEGY = LongPasswordStrategies.truncate(VERSION);
    private static final BCrypt.Hasher HASHER = BCrypt.with(VERSION, new SecureRandom(), STRATEGY);
    private static final BCrypt.Verifyer VERIFYER = BCrypt.verifyer(VERSION, STRATEGY);

    private final Vertx vertx;

    public PasswordHashingService(Vertx vertx) {
        this.vertx = vertx;
    }

    public Future<String> hashPassword(String plainPassword) {
        return vertx.executeBlocking(() -> HASHER.hashToString(10, plainPassword.toCharArray()));
    }

    public Future<Boolean> verifyPassword(String hashedPassword, String plainPassword) {
        return vertx.executeBlocking(() -> VERIFYER.verify(plainPassword.toCharArray(), hashedPassword.toCharArray()))
                .map(result -> result.verified);
    }
}
