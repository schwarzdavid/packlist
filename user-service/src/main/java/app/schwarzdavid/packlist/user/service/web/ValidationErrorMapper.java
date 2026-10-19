package app.schwarzdavid.packlist.user.service.web;

import app.schwarzdavid.packlist.user.api.model.ProblemDetailsFieldDto;
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
        return ProblemDetailsFieldDto.builder().error(error).build();
    }

    private ProblemDetailsFieldDto mapSize(Size size, ConstraintViolation<?> violation) {
        var length = ((String) violation.getInvalidValue()).length();
        if (length < size.min()) {
            if (size.min() == 1) {
                return withoutArguments(REQUIRED);
            }
            return ProblemDetailsFieldDto.builder().error(MIN_LENGTH).params(Map.of("min", size.min())).build();
        }
        return ProblemDetailsFieldDto.builder().error(MAX_LENGTH).params(Map.of("max", size.max())).build();
    }
}
