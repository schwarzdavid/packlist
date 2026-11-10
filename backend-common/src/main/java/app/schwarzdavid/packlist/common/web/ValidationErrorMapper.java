package app.schwarzdavid.packlist.common.web;

import app.schwarzdavid.packlist.common.web.dto.ProblemDetailsFieldDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.experimental.UtilityClass;
import org.springframework.validation.FieldError;

import java.util.Map;

@UtilityClass
public class ValidationErrorMapper {
    private final String INVALID = "validation.invalid";
    private final String REQUIRED = "validation.required";
    private final String EMAIL = "validation.email";
    private final String MIN_LENGTH = "validation.minLength";
    private final String MAX_LENGTH = "validation.maxLength";

    public ProblemDetailsFieldDto mapFieldError(FieldError fieldError) {
        if (!fieldError.contains(ConstraintViolation.class)) {
            return withoutArguments(INVALID);
        }

        var violation = fieldError.unwrap(ConstraintViolation.class);

        return switch (violation.getConstraintDescriptor().getAnnotation()) {
            case NotNull ignored -> withoutArguments(REQUIRED);
            case Email ignored -> withoutArguments(EMAIL);
            case Size size -> mapSize(size, violation);
            default -> withoutArguments(INVALID);
        };
    }

    private ProblemDetailsFieldDto withoutArguments(String error) {
        var field = new ProblemDetailsFieldDto();
        field.setError(error);
        return field;
    }

    private ProblemDetailsFieldDto mapSize(Size size, ConstraintViolation<?> violation) {
        var length = ((String) violation.getInvalidValue()).length();
        if (length < size.min()) {
            if (size.min() == 1) {
                return withoutArguments(REQUIRED);
            }
            var field = new ProblemDetailsFieldDto();
            field.setError(MIN_LENGTH);
            field.setParams(Map.of("min", size.min()));
            return field;
        }
        var field = new ProblemDetailsFieldDto();
        field.setError(MAX_LENGTH);
        field.setParams(Map.of("max", size.max()));
        return field;
    }
}
