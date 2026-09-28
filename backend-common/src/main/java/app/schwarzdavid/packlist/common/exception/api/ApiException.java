package app.schwarzdavid.packlist.common.exception.api;

import lombok.Getter;

public class ApiException extends RuntimeException {
    @Getter
    private final int statusCode;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
