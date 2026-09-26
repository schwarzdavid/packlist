package app.schwarzdavid.packlist.service.user;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.common.Database;
import app.schwarzdavid.packlist.common.Migrator;
import app.schwarzdavid.packlist.service.user.runtime.HttpVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;
import io.vertx.sqlclient.Pool;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    static void main(String[] args) {
        var defaultDatabaseConfig = new Config.DatabaseConfig("localhost", 5432, "users", "users", "users");

        var vertx = Vertx.vertx();
        Config.create(vertx)
                .compose(config -> Migrator.migrate(vertx, config.getDatabaseConfig("user", defaultDatabaseConfig)).map(config))
                .compose(config -> {
                    Pool pool = Database.create(vertx, config.getDatabaseConfig("user", defaultDatabaseConfig));

                    return vertx.deployVerticle(
                            new HttpVerticle(config, pool),
                            new DeploymentOptions()
                                    .setConfig(config.getValues())
                    );
                })
                .onSuccess(v -> logger.info("Application started"))
                .onFailure(Throwable::printStackTrace);
    }
}
