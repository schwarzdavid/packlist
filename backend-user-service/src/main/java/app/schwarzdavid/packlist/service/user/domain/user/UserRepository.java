package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.common.exception.PostgresExceptionHandler;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserDataWithPassword;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Map;
import java.util.Optional;

public class UserRepository {
    private final Pool pool;

    public UserRepository(Pool pool) {
        this.pool = pool;
    }

    public Future<UserData> createUser(UserCreateData userCreateDto, String hashedPassword) {
        Map<String, Object> params = Map.of("username", userCreateDto.username().trim(), "email", userCreateDto.email().trim(), "password", hashedPassword);

        return SqlTemplate
                .forUpdate(pool, "INSERT INTO users (username, email, password) VALUES (#{username}, #{email}, #{password}) RETURNING *")
                .mapTo(UserData.class)
                .execute(params)
                .map(result -> result.iterator().next())
                .recover(PostgresExceptionHandler::recover);
    }

    public Future<Optional<UserDataWithPassword>> findUserWithPasswordByIdentifier(String identifier) {
        return pool.preparedQuery("SELECT * FROM users WHERE username=$1 OR email=$1 LIMIT 1")
                .execute(Tuple.of(identifier.trim()))
                .map(result -> {
                    var rowOptional = result.stream().findFirst();

                    if (rowOptional.isEmpty()) {
                        return Optional.empty();
                    }

                    var row = rowOptional.get();
                    return Optional.of(new UserDataWithPassword(
                            row.getLong("userId"),
                            row.getString("username"),
                            row.getString("email"),
                            row.getString("password")
                    ));
                });
    }
}
