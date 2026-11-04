package app.schwarzdavid.packlist.list.service.fixture;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.HashSet;

@IntegrationTest
public abstract class ApiTest {
    private static final Logger logger = LoggerFactory.getLogger(ApiTest.class);
    @Autowired
    protected RestTestClient client;
    @Value("${USER_SERVICE_HOSTNAME}")
    private String userServiceHostname;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        var tableNames = new HashSet<String>();

        entityManagerFactory.unwrap(SessionFactoryImplementor.class)
                .getMappingMetamodel()
                .forEachEntityDescriptor(entity -> tableNames.add(entity.getTableName()));

        var joinedTableNames = String.join(", ", tableNames);

        logger.info("Truncating tables {}", joinedTableNames);
        jdbcTemplate.execute("TRUNCATE TABLE %s RESTART IDENTITY CASCADE".formatted(joinedTableNames));
        logger.info("Tables truncated");
    }

    protected String getJwt() {
        return client.get().uri("%s/test-user".formatted(userServiceHostname)).exchange()
                .expectStatus().isOk()
                .returnResult(String.class).getResponseBody();
    }
}
