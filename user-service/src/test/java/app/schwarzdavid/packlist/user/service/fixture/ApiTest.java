package app.schwarzdavid.packlist.user.service.fixture;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.HashSet;

@IntegrationTest
public abstract class ApiTest {
    private static final Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Autowired
    protected RestTestClient client;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        var tableNames = new HashSet<String>();
        var mappingMetamodel = entityManagerFactory.unwrap(SessionFactoryImplementor.class).getMappingMetamodel();

        mappingMetamodel.forEachEntityDescriptor(entity -> {
            tableNames.add(entity.getTableName());
        });

        jdbcTemplate.execute("TRUNCATE TABLE %s RESTART IDENTITY CASCADE".formatted(String.join(", ", tableNames)));
    }
}
