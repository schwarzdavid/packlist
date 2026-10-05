package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.common.JwtService;
import app.schwarzdavid.packlist.service.user.service.security.PasswordHashingService;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.sqlclient.Pool;

public record UserModule(
        Router router,
        UserService userService
) {
    public static UserModule create(Vertx vertx, Pool pool, OpenAPIContract contract, JwtService jwtService, PasswordHashingService passwordHashingService) {
        var userRepository = new UserRepository(pool);
        var userService = new UserService(userRepository, passwordHashingService);
        var userHandler = new UserHandler(userService);
        var userRouter = new UserRouter(userHandler, contract, jwtService);

        return new UserModule(userRouter.createRouter(vertx), userService);
    }
}
