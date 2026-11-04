package app.schwarzdavid.packlist.user.service.fixture;

import app.schwarzdavid.packlist.user.service.domain.auth.JwtService;
import app.schwarzdavid.packlist.user.service.domain.auth.data.AuthData;
import app.schwarzdavid.packlist.user.service.domain.user.UserRepository;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import app.schwarzdavid.packlist.user.service.domain.user.entity.UserEntity;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    public void setup() {
        var tableNames = new HashSet<String>();
        var mappingMetamodel = entityManagerFactory.unwrap(SessionFactoryImplementor.class).getMappingMetamodel();

        mappingMetamodel.forEachEntityDescriptor(entity -> tableNames.add(entity.getTableName()));

        logger.info("Truncating tables {}", String.join(", ", tableNames));
        jdbcTemplate.execute("TRUNCATE TABLE %s RESTART IDENTITY CASCADE".formatted(String.join(", ", tableNames)));
        logger.info("Tables truncated");
    }

    protected UserData createUser() {
        var hashedPassword = passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD);
        return createUser(new UserCreateData(UserFixtures.USERNAME, UserFixtures.EMAIL, hashedPassword));
    }

    protected UserData createUser(UserCreateData userCreateData) {
        var entity = UserEntity.builder()
                .username(userCreateData.username())
                .email(userCreateData.email())
                .password(userCreateData.hashedPassword())
                .build();
        entity = userRepository.save(entity);

        return new UserData(entity.getUserId(), entity.getUsername(), entity.getEmail());
    }

    protected AuthData createAuth() {
        var hashedPassword = passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD);
        return createAuth(new UserCreateData(UserFixtures.USERNAME, UserFixtures.EMAIL, hashedPassword));
    }

    protected AuthData createAuth(UserCreateData userCreateData) {
        var user = createUser(userCreateData);
        var token = jwtService.createJwtForUser(user);

        return new AuthData(user.userId(), token, user);
    }
}
