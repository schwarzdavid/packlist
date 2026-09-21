package app.schwarzdavid.packlist.service.user.runtime;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class HttpVerticle extends VerticleBase {
    private final Logger logger = LoggerFactory.getLogger(HttpVerticle.class);

    @Override
    public Future<?> start() {
        var router = Router.router(vertx);

        return vertx.createHttpServer()
                .requestHandler(router)
                .listen(8881)
                .onSuccess(server -> logger.info("Server started on port %d".formatted(server.actualPort())))
                .onFailure(err -> logger.error("Failed to start server", err));
    }
}
