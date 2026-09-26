package app.schwarzdavid.packlist.common;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import org.jspecify.annotations.NonNull;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private final JsonObject values;

    private Config(JsonObject values) {
        this.values = values;
    }

    public static Future<Config> create(Vertx vertx) {
        var options = new ConfigRetrieverOptions()
                .setIncludeDefaultStores(false);

        var envFileLocation = getEnvFileLocation(Path.of("").toAbsolutePath());
        if(envFileLocation == null) {
            logger.info("No environment file location found.");
        } else {
            var localEnv = new ConfigStoreOptions()
                    .setType("file")
                    .setFormat("properties")
                    .setOptional(false)
                    .setConfig(new JsonObject().put("path", envFileLocation));
            options.addStore(localEnv);
        }

        var systemEnv = new ConfigStoreOptions()
                .setType("env");
        options.addStore(systemEnv);

        var retriever = ConfigRetriever.create(vertx, options);

        return retriever.getConfig().map(Config::new);
    }

    static String getEnvFileLocation(Path startDir) {
        Path dir = startDir;
        while(dir != null) {
            Path candidate = dir.resolve(".env");
            if(Files.isRegularFile(candidate)) {
                return candidate.toString();
            }
            dir = dir.getParent();
        }
        return null;
    }

    public JsonObject getValues() {
        return values;
    }

    public DatabaseConfig getDatabaseConfig(String prefix) {
        return getDatabaseConfig(prefix, new DatabaseConfig(null, null, null, null, null));
    }

    public DatabaseConfig getDatabaseConfig(String prefix, @NonNull DatabaseConfig defaults) {
        String normalizedPrefix = prefix.toUpperCase().trim();
        return new DatabaseConfig(
                values.getString(normalizedPrefix + "_POSTGRES_HOST", defaults.host),
                values.getInteger(normalizedPrefix + "_POSTGRES_PORT", defaults.port),
                values.getString(normalizedPrefix + "_POSTGRES_DB", defaults.database),
                values.getString(normalizedPrefix + "_POSTGRES_USER", defaults.user),
                values.getString(normalizedPrefix + "_POSTGRES_PASSWORD", defaults.password)
        );
    }

    public record DatabaseConfig (
            String host,
            Integer port,
            String database,
            String user,
            String password
    ) { }
}
