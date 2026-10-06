package app.schwarzdavid.packlist.common.infrastructure;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public final class Database {
    public static Pool create(Vertx vertx, Config.DatabaseConfig dbConfig, int maxPoolSize) {
        var connectOptions = new PgConnectOptions()
                .setHost(dbConfig.host())
                .setPort(dbConfig.port())
                .setDatabase(dbConfig.database())
                .setUser(dbConfig.user())
                .setPassword(dbConfig.password());

        var poolOptions = new PoolOptions().setMaxSize(maxPoolSize);

        return PgBuilder.pool().with(poolOptions).connectingTo(connectOptions).using(vertx).build();
    }

    public static Pool create(Vertx vertx, Config.DatabaseConfig config) {
        return create(vertx, config, 10);
    }
}
