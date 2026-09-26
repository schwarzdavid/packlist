package app.schwarzdavid.packlist.common;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(VertxExtension.class)
public class ConfigTest {
    @TempDir
    private Path tempDir;

    @Test
    void shouldFindEnvFileLocationWhenItExists() throws IOException {
        var envFile = tempDir.resolve(".env");
        var child = tempDir.resolve("foo").resolve("bar");

        Files.writeString(envFile, "FOO=bar\r\n");
        Files.createDirectories(child);

        var result = Config.getEnvFileLocation(tempDir);

        assertEquals(envFile.toString(), result);
    }

    @Test
    void shouldReturnDefaultWhenEnvFileDoesNotExist() throws IOException {
        var child = tempDir.resolve("foo").resolve("bar");

        Files.createDirectories(child);

        var result = Config.getEnvFileLocation(child);

        assertNull(result);
    }

    @Test
    void shouldCreateConfigRetrieverWithEnvFileStore(Vertx vertx) throws IOException {
        var envFile = tempDir.resolve(".env");

        try (var mockedRetriever = mockStatic(ConfigRetriever.class, CALLS_REAL_METHODS); var mockedConfig = mockStatic(Config.class, CALLS_REAL_METHODS)) {
            mockedConfig.when(() -> Config.getEnvFileLocation(any())).thenReturn(envFile.toString());

            Config.create(vertx);

            mockedRetriever.verify(() -> ConfigRetriever.create(any(), assertArg(options -> {
                assertEquals(2, options.getStores().size());
                assertEquals("file", options.getStores().getFirst().getType());
                assertEquals(envFile.toString(), options.getStores().getFirst().getConfig().getString("path"));
                assertEquals("env", options.getStores().get(1).getType());
            })));
        }
    }

    @Test
    void shouldCreateConfigRetrieverWithoutEnvFileStore(Vertx vertx) {
        try (var mockedRetriever = mockStatic(ConfigRetriever.class, CALLS_REAL_METHODS); var mockedConfig = mockStatic(Config.class, CALLS_REAL_METHODS)) {
            mockedConfig.when(() -> Config.getEnvFileLocation(any())).thenReturn(null);

            Config.create(vertx);

            mockedRetriever.verify(() -> ConfigRetriever.create(any(), assertArg(options -> {
                assertEquals(1, options.getStores().size());
                assertEquals("env", options.getStores().getFirst().getType());
            })));
        }
    }
}
