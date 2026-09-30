package app.schwarzdavid.packlist.service.user.domain.auth.exception;

import app.schwarzdavid.packlist.common.exception.api.ApiException;

public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException() {
        super(401, "Invalid Credentials");
    }

    public InvalidCredentialsException(Throwable cause) {
        super(401, "Invalid Credentials", cause);
    }
}
