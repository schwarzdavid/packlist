package app.schwarzdavid.packlist.user.service.fixture;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.postgresql.PostgreSQLContainer;

public interface UserServiceTestcontainers {
    @ServiceConnection
    PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:18-alpine");
}
