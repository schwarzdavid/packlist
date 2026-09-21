package app.schwarzdavid.packlist.common;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public final class Config {
    private final JsonObject values;

    private Config(JsonObject values) {
        this.values = values;
    }

    public static Future<Config> create(Vertx vertx) {
        var localEnv = new ConfigStoreOptions()
                .setType("file")
                .setFormat("properties")
                .setOptional(true)
                .setConfig(new JsonObject().put("path", ".env"));

        var systemEnv = new ConfigStoreOptions()
                .setType("env");

        var options = new ConfigRetrieverOptions()
                .setIncludeDefaultStores(false)
                .addStore(localEnv)
                .addStore(systemEnv);

        var retriever = ConfigRetriever.create(vertx, options);

        return retriever.getConfig().map(Config::new);
    }

    public JsonObject getValues() {
        return values;
    }

    public DatabaseConfig getDatabaseConfig(String prefix) {
        return new DatabaseConfig(
                values.getString(prefix + "_POSTGRES_HOST", "localhost"),
                values.getInteger(prefix + "_POSTGRES_PORT"),
                values.getString(prefix + "_POSTGRES_DATABASE"),
                values.getString(prefix + "_POSTGRES_USER"),
                values.getString(prefix + "_POSTGRES_PASSWORD")
        );
    }

    public record DatabaseConfig (
            String host,
            int port,
            String database,
            String user,
            String password
    ) { }
}
