package app.schwarzdavid.packlist.common.test.extension;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;

public class DatabaseTruncateExtension implements BeforeEachCallback {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseTruncateExtension.class);

    @Override
    public void beforeEach(@NonNull ExtensionContext context) {
        var applicationContext = SpringExtension.getApplicationContext(context);
        var entityManagerFactory = applicationContext.getBean(EntityManagerFactory.class);
        var jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
        var tableNames = new HashSet<String>();

        entityManagerFactory.unwrap(SessionFactoryImplementor.class)
                .getMappingMetamodel()
                .forEachEntityDescriptor(entity -> tableNames.add(entity.getTableName()));

        if (tableNames.isEmpty()) {
            return;
        }

        var joinedTableNames = String.join(", ", tableNames);

        logger.info("Truncating tables {}", joinedTableNames);
        jdbcTemplate.execute("TRUNCATE TABLE %s RESTART IDENTITY CASCADE".formatted(joinedTableNames));
        logger.info("Tables truncated");
    }
}
