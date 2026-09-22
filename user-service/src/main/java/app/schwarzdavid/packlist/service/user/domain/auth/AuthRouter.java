package app.schwarzdavid.packlist.service.user.domain.auth;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class AuthRouter {
    private final AuthHandler authHandler;

    public AuthRouter(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

    public Router createRouter(Vertx vertx) {
        var router = Router.router(vertx);

        router.post("/login").handler(authHandler::login);

        return router;
    }
}
