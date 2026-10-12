package app.schwarzdavid.packlist.user.service.fixture;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;

public interface ServiceTestcontainers {
    @ServiceConnection
    @Container
    PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:18-alpine");
}
