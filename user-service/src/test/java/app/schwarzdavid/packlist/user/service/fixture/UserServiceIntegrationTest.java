package app.schwarzdavid.packlist.user.service.fixture;

import app.schwarzdavid.packlist.common.test.extension.DatabaseTruncateExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportTestcontainers(UserServiceTestcontainers.class)
@TestPropertySource(properties = "management.server.port=-1")
@AutoConfigureRestTestClient
@ExtendWith(DatabaseTruncateExtension.class)
public @interface UserServiceIntegrationTest {
}
