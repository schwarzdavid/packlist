package app.schwarzdavid.packlist.service.user.domain.user;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public final class UserRouter {
    private final UserHandler userHandler;

    public UserRouter(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    public Router createRouter(Vertx vertx) {
        var router = Router.router(vertx);

        router.post("/users").respond(userHandler::createUser);

        return router;
    }
}
