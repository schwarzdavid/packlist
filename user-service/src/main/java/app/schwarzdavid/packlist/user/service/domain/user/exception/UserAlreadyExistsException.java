package app.schwarzdavid.packlist.user.service.domain.user.exception;

import lombok.Getter;

public class UserAlreadyExistsException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "User already exists";
    @Getter
    private final Field field;

    public UserAlreadyExistsException(Field field) {
        this(field, DEFAULT_MESSAGE);
    }

    public UserAlreadyExistsException(Field field, String message) {
        super(message);
        this.field = field;
    }

    public enum Field {
        EMAIL("email"),
        USERNAME("username");

        @Getter
        private final String propertyName;

        Field(String propertyName) {
            this.propertyName = propertyName;
        }
    }
}
