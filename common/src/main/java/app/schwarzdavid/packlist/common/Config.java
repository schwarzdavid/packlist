package app.schwarzdavid.packlist.common;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

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
                .setConfig(new JsonObject().put("path", getEnvFileLocation()));

        var systemEnv = new ConfigStoreOptions()
                .setType("env");

        var options = new ConfigRetrieverOptions()
                .setIncludeDefaultStores(false)
                .addStore(localEnv)
                .addStore(systemEnv);

        var retriever = ConfigRetriever.create(vertx, options);

        return retriever.getConfig().map(Config::new);
    }

    private static String getEnvFileLocation() {
        return Optional.ofNullable(System.getProperty("packlist.env"))
                .or(() -> Optional.ofNullable(System.getenv("PACKLIST_ENV_FILE")))
                .orElseGet(Config::findEnvFileLocation);
    }

    private static String findEnvFileLocation() {
        Path dir = Path.of("").toAbsolutePath();
        while(dir != null) {
            Path candidate = dir.resolve(".env");
            if(Files.isRegularFile(candidate)) {
                return candidate.toString();
            }
            dir = dir.getParent();
        }
        return ".env";
    }

    public JsonObject getValues() {
        return values;
    }

    public DatabaseConfig getDatabaseConfig(String prefix) {
        String normalizedPrefix = prefix.toUpperCase().trim();
        return new DatabaseConfig(
                values.getString(normalizedPrefix + "_POSTGRES_HOST", "localhost"),
                values.getInteger(normalizedPrefix + "_POSTGRES_PORT", 5432),
                values.getString(normalizedPrefix + "_POSTGRES_DB", "users"),
                values.getString(normalizedPrefix + "_POSTGRES_USER", "users"),
                values.getString(normalizedPrefix + "_POSTGRES_PASSWORD", "users")
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
