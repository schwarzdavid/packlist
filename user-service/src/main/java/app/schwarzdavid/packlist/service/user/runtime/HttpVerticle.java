package app.schwarzdavid.packlist.service.user.runtime;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.service.user.domain.auth.AuthModule;
import app.schwarzdavid.packlist.service.user.domain.user.UserModule;
import app.schwarzdavid.packlist.service.user.service.security.PasswordHashingService;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;

public class HttpVerticle extends VerticleBase {
    private final Logger logger = LoggerFactory.getLogger(HttpVerticle.class);
    private final Config config;
    private final Pool pool;

    public HttpVerticle(Config config, Pool pool) {
        this.config = config;
        this.pool = pool;
    }

    @Override
    public Future<?> start() {
        var router = Router.router(vertx);

        var passwordHashingService = new PasswordHashingService(vertx);

        var userModule = UserModule.create(vertx, pool, passwordHashingService);
        var authModule = AuthModule.create(vertx, pool, config, userModule, passwordHashingService);

        router.route().handler(BodyHandler.create());
        router.route("/api/*").subRouter(authModule.router());
        router.route("/api/*").subRouter(userModule.router());

        return vertx.createHttpServer()
                .requestHandler(router)
                .listen(8881)
                .onSuccess(server -> logger.info("Server started on port %d".formatted(server.actualPort())))
                .onFailure(err -> logger.error("Failed to start server", err));
    }
}
