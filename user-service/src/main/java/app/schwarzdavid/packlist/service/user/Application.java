package app.schwarzdavid.packlist.service.user;

import app.schwarzdavid.packlist.common.Config;
import app.schwarzdavid.packlist.common.Database;
import app.schwarzdavid.packlist.service.user.runtime.HttpVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;
import io.vertx.sqlclient.Pool;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    static void main(String[] args) {
        var vertx = Vertx.vertx();
        Config.create(vertx)
                .compose(config -> {
                    Pool pool = Database.create(vertx, config, "user");

                    return vertx.deployVerticle(
                            new HttpVerticle(),
                            new DeploymentOptions()
                                    .setConfig(config.getValues())
                    );
                })
                .onSuccess(v -> logger.info("Application started"))
                .onFailure(Throwable::printStackTrace);
    }
}
