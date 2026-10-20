package app.schwarzdavid.packlist.list.service.fixture;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;

public interface ServiceTestcontainers {
    @ServiceConnection
    PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:18-alpine");

    PostgreSQLContainer userServicePostgresqlContainer = new PostgreSQLContainer("postgres:18-alpine");

}
