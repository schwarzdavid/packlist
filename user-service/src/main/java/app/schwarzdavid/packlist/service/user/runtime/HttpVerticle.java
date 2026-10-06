package app.schwarzdavid.packlist.service.user.runtime;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.common.JwtService;
import app.schwarzdavid.packlist.common.exception.RouterExceptionHandler;
import app.schwarzdavid.packlist.service.user.domain.auth.AuthModule;
import app.schwarzdavid.packlist.service.user.domain.user.UserModule;
import app.schwarzdavid.packlist.service.user.service.security.PasswordHashingService;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.json.schema.Draft;
import io.vertx.json.schema.JsonSchemaOptions;
import io.vertx.json.schema.SchemaRepository;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.sqlclient.Pool;
import lombok.Getter;

public class HttpVerticle extends VerticleBase {
    private final Logger logger = LoggerFactory.getLogger(HttpVerticle.class);
    private final Config config;

    @Getter
    private final Pool pool;

    @Getter
    private Integer port;

    public HttpVerticle(Config config, Pool pool) {
        this.config = config;
        this.pool = pool;
    }

    @Override
    public Future<?> start() {
        var router = Router.router(vertx);

        var schemaRepository = SchemaRepository.create(new JsonSchemaOptions().setDraft(Draft.DRAFT7).setBaseUri("https://schemas.packlist.app"));
        var passwordHashingService = new PasswordHashingService(vertx);

        return OpenAPIContract.from(vertx, "users-api.yml")
                .compose(contract -> {
                    var jwtService = new JwtService(vertx, config);
                    var userModule = UserModule.create(vertx, pool, contract, jwtService, passwordHashingService);
                    var authModule = AuthModule.create(vertx, userModule, jwtService, passwordHashingService, schemaRepository);

                    router.route().handler(BodyHandler.create());
                    router.route("/api/*").subRouter(authModule.router());
                    router.route().subRouter(userModule.router());
                    router.route().failureHandler(RouterExceptionHandler::handleFailure);

                    return vertx.createHttpServer()
                            .requestHandler(router)
                            .listen(config.getValues().getInteger("HTTP_PORT", 8881));

                })
                .onSuccess(server -> {
                    port = server.actualPort();
                    logger.info("Server started on port %d".formatted(server.actualPort()));
                })
                .onFailure(err -> logger.error("Failed to start server", err));
    }

    @Override
    public Future<?> stop() throws Exception {
        port = null;

        return super.stop();
    }
}
