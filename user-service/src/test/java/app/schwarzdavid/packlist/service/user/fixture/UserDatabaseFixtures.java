package app.schwarzdavid.packlist.service.user.fixture;

import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserDataWithPassword;
import app.schwarzdavid.packlist.service.user.service.security.PasswordHashingService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;

public class UserDatabaseFixtures {
    private UserDatabaseFixtures() {}

    public static Future<UserDataWithPassword> create(Vertx vertx, Pool pool) {
        return create(vertx, pool, UserFixtures.create());
    }

    public static Future<UserDataWithPassword> create(Vertx vertx, Pool pool, UserCreateData userCreateData) {
        var passwordHashingService = new PasswordHashingService(vertx);

        return passwordHashingService.hashPassword(userCreateData.password())
                .compose(hashedPassword -> pool.preparedQuery("INSERT INTO users (username, email, password) VALUES ($1, $2, $3) RETURNING *").execute(Tuple.of(userCreateData.username(), userCreateData.email(), hashedPassword)))
                .map(rowSet -> rowSet.iterator().next().toJson().mapTo(UserDataWithPassword.class));
    }
}
