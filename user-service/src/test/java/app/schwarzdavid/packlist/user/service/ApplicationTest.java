package app.schwarzdavid.packlist.user.service;

import app.schwarzdavid.packlist.user.service.fixture.ServiceTestcontainers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

@ImportTestcontainers(ServiceTestcontainers.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    @Test
    void contextLoads() {
        // Test that the application context loads
    }
}
