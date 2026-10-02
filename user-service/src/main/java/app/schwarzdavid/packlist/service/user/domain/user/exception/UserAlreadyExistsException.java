package app.schwarzdavid.packlist.service.user.domain.user.exception;

import app.schwarzdavid.packlist.common.exception.api.ApiException;
import lombok.Getter;

public class UserAlreadyExistsException extends ApiException {
    @Getter
    private final Field field;

    public UserAlreadyExistsException(Field field) {
        super(409, getFieldMessage(field));
        this.field = field;
    }

    public UserAlreadyExistsException(Field field, Throwable cause) {
        super(409, getFieldMessage(field), cause);
        this.field = field;
    }

    private static String getFieldMessage(Field field) {
        return switch (field) {
            case EMAIL -> "User with this e-mail address already exists";
            case USERNAME -> "User with this username already exists";
        };
    }

    public enum Field {
        USERNAME,
        EMAIL
    }
}
