package app.schwarzdavid.packlist.common;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;
import org.flywaydb.core.Flyway;

public final class Migrator {
    private static final Logger logger = LoggerFactory.getLogger(Migrator.class);

    public static Future<?> migrate(Vertx vertx, Config.DatabaseConfig dbConfig) {
        var url = "jdbc:postgresql://%s:%d/%s".formatted(dbConfig.host(), dbConfig.port(), dbConfig.database());

        logger.info("Migrating database at %s".formatted(url));

        return vertx.executeBlocking(() -> {
            Flyway.configure()
                    .validateMigrationNaming(true)
                    .dataSource(url, dbConfig.user(), dbConfig.password())
                    .load()
                    .migrate();
            logger.info("Database migration completed");
            return null;
        });
    }
}
