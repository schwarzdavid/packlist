package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserCreateDto;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
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
}
