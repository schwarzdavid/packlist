package app.schwarzdavid.packlist.common.exception;

import app.schwarzdavid.packlist.common.exception.db.UniqueConstraintValidationException;
import io.vertx.core.Future;
import io.vertx.pgclient.PgException;

public class PostgresExceptionHandler {
    private static final String ERROR_CODE_UNIQUE_CONSTRAINT = "23505";
    private static final String ERROR_CODE_FOREIGN_KEY_CONSTRAINT = "23503";
    private static final String ERROR_CODE_NOT_NULL_CONSTRAINT = "23502";

    private PostgresExceptionHandler() {
    }

    public static <T> Future<T> recover(Throwable throwable) {
        if (throwable instanceof PgException pgException) {
            var ex = switch (pgException.getSqlState()) {
                case ERROR_CODE_UNIQUE_CONSTRAINT ->
                        new UniqueConstraintValidationException(pgException.getConstraint(), pgException.getMessage(), pgException);
                default -> null;
            };

            if (ex != null) {
                return Future.failedFuture(ex);
            }
        }

        return Future.failedFuture(throwable);
    }
}
