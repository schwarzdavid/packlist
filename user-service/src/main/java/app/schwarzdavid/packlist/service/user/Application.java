package app.schwarzdavid.packlist.service.user;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.common.Database;
import app.schwarzdavid.packlist.common.Migrator;
import app.schwarzdavid.packlist.service.user.runtime.HttpVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    static void main(String[] args) {
        var vertx = Vertx.vertx();
        Config.create(vertx)
                .compose(config -> start(vertx, config))
                .onSuccess(v -> logger.info("Application started"))
                .onFailure(exception -> {
                    logger.error("Application failed to start", exception);
                    System.exit(1);
                });
    }

    public static Future<HttpVerticle> start(Vertx vertx, Config config) {
        var defaultDatabaseConfig = new Config.DatabaseConfig("localhost", 5432, "users", "users", "users");
        var databaseConfig = config.getDatabaseConfig("user", defaultDatabaseConfig);

        return Migrator.migrate(vertx, databaseConfig)
                .compose(_ -> {
                    var pool = Database.create(vertx, databaseConfig);
                    var verticle = new HttpVerticle(config, pool);

                    return vertx.deployVerticle(
                            verticle,
                            new DeploymentOptions()
                                    .setConfig(config.getValues())
                    ).map(id -> verticle);
                });
    }
}
