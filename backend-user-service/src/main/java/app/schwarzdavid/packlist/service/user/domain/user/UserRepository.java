package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserDataWithPassword;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserCreateDto;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Map;

public class UserRepository {
    private final Pool pool;

    public UserRepository(Pool pool) {
        this.pool = pool;
    }

    public Future<UserData> createUser(UserCreateDto userCreateDto,  String hashedPassword) {
        Map<String, Object> params = Map.of("username", userCreateDto.username(), "email", userCreateDto.email(), "password", hashedPassword);

        return SqlTemplate
                .forUpdate(pool, "INSERT INTO users (username, email, password) VALUES (#{username}, #{email}, #{password}) RETURNING *")
                .mapTo(UserData.class)
                .execute(params)
                .map(result -> result.iterator().next());
    }

    public Future<UserDataWithPassword> findUserWithPasswordByIdentifier(String identifier) {
        return pool.preparedQuery("SELECT * FROM users WHERE username=$1 OR email=$1 LIMIT 1")
                .execute(Tuple.of(identifier))
                .map(result -> {
                    var row = result.stream().findFirst().orElseThrow();

                    return new UserDataWithPassword(
                            row.getLong("userId"),
                            row.getString("username"),
                            row.getString("email"),
                            row.getString("password")
                    );
                });
    }
}
