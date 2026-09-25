package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.service.user.domain.user.UserModule;
import app.schwarzdavid.packlist.service.user.service.security.PasswordHashingService;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

public record AuthModule(
        Router router,
        JwtService jwtService
) {
    public static AuthModule create(Vertx vertx, Pool pool, Config config, UserModule userModule, PasswordHashingService passwordHashingService) {
        var authenticationRepository = new AuthRepository(pool);
        var jwtService = new JwtService(vertx, config);
        var credentialsService = new CredentialsService(authenticationRepository, userModule.userService(), passwordHashingService, jwtService);
        var authenticationHandler = new AuthHandler(credentialsService, userModule.userService(), jwtService);
        var authenticationRouter = new AuthRouter(authenticationHandler);

        return new AuthModule(
                authenticationRouter.createRouter(vertx),
                jwtService
        );
    }
}
