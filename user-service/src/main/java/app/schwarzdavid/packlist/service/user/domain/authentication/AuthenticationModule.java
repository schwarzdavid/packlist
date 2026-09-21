package app.schwarzdavid.packlist.service.user.domain.authentication;

import io.vertx.core.Vertx;

public record AuthenticationModule(
        PasswordHashingService passwordHashingService
) {
    public static AuthenticationModule create(Vertx vertx) {
        PasswordHashingService passwordHashingService = new PasswordHashingService(vertx);

        return new AuthenticationModule(passwordHashingService);
    }
}
