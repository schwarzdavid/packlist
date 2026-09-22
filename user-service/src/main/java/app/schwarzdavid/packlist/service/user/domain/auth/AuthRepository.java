package app.schwarzdavid.packlist.service.user.domain.auth;

import io.vertx.sqlclient.Pool;

public class AuthRepository {
    private final Pool pool;

    public AuthRepository(Pool pool) {
        this.pool = pool;
    }
}
