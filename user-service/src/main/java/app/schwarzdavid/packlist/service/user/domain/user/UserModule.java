package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.service.user.domain.authentication.AuthenticationModule;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

public record UserModule(
        Router router,
        UserService userService
) {
    public static UserModule create(Vertx vertx, Pool pool, AuthenticationModule authenticationModule) {
        var userRepository = new UserRepository(pool);
        var userService = new UserService(userRepository, authenticationModule.passwordHashingService());
        var userHandler = new UserHandler(userService);
        var userRouter = new UserRouter(userHandler);

        return new UserModule(userRouter.createRouter(vertx), userService);
    }
}
