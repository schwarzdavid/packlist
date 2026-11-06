package app.schwarzdavid.packlist.list.service.fixture;

import app.schwarzdavid.packlist.common.test.extension.DatabaseTruncateExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.TestPropertySource;
import org.wiremock.extension.jwt.JwtExtensionFactory;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock(
        @ConfigureWireMock(
                baseUrlProperties = "USER_SERVICE_HOSTNAME",
                extensionFactories = JwtExtensionFactory.class
        )
)
@TestPropertySource(properties = "management.server.port=-1")
@ImportTestcontainers(ListServiceTestcontainers.class)
@AutoConfigureRestTestClient
@ExtendWith(DatabaseTruncateExtension.class)
public @interface ListServiceIntegrationTest {
}
