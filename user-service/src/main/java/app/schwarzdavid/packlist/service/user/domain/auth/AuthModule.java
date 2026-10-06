package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.common.JwtService;
import app.schwarzdavid.packlist.service.user.domain.user.UserModule;
import app.schwarzdavid.packlist.service.user.infrastructure.security.PasswordHashingService;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.json.schema.SchemaRepository;

public record AuthModule(
        Router router
) {
    public static AuthModule create(
            Vertx vertx,
            UserModule userModule,
            JwtService jwtService,
            PasswordHashingService passwordHashingService,
            SchemaRepository schemaRepository
    ) {
        var credentialsService = new CredentialsService(userModule.userService(), passwordHashingService, jwtService);
        var authService = new AuthService(userModule.userService(), jwtService);
        var authHandler = new AuthHandler(credentialsService, authService);
        var authRouter = new AuthRouter(authHandler, schemaRepository);

        return new AuthModule(
                authRouter.createRouter(vertx)
        );
    }
}
