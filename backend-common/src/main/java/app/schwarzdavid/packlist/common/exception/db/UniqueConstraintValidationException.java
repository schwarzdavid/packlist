package app.schwarzdavid.packlist.common.exception.db;

import lombok.Getter;

public class UniqueConstraintValidationException extends RuntimeException {
    @Getter
    private final String constraint;

    public UniqueConstraintValidationException(String constraint, String message, Throwable cause) {
        super(message, cause);
        this.constraint = constraint;
    }

    public UniqueConstraintValidationException(String constraint, String message) {
        super(message);
        this.constraint = constraint;
    }
}
