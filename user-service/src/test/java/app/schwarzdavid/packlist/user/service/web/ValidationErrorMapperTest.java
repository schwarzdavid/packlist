package app.schwarzdavid.packlist.user.service.web;

import jakarta.validation.Validation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationErrorMapperTest {
    static Stream<Arguments> testCase() {
        return Stream.of(
                Arguments.of("required", null, "validation.required", Map.of()),
                Arguments.of("sized", "ab", "validation.minLength", Map.of("min", 3)),
                Arguments.of("sized", "abcdefghijklmnopqrstuvwxyz", "validation.maxLength", Map.of("max", 10)),
                Arguments.of("email", "no-email", "validation.email", Map.of()),
                Arguments.of("pattern", "email@example.com", "validation.invalid", Map.of())
        );
    }

    @ParameterizedTest
    @MethodSource("testCase")
    void shouldMapFieldError(String field, String value, String expectedError, Object expectedParams) {
        var validator = new SpringValidatorAdapter(Validation.buildDefaultValidatorFactory().getValidator());
        var target = getPrefilledValidationTarget(field, value);
        var errors = new BeanPropertyBindingResult(target, "");

        validator.validate(target, errors);

        var problemDetailsField = ValidationErrorMapper.mapFieldError(Objects.requireNonNull(errors.getFieldError(field)));

        assertThat(problemDetailsField).isNotNull();
        assertThat(problemDetailsField.getError()).isEqualTo(expectedError);
        assertThat(problemDetailsField.getParams()).isEqualTo(expectedParams);
    }

    private ValidationTarget getPrefilledValidationTarget(String field, String value) {
        ValidationTarget target = new ValidationTarget();
        ReflectionTestUtils.setField(target, field, value);
        return target;
    }

    private static final class ValidationTarget {
        @NotNull
        String required;
        @Size(min = 3, max = 10)
        String sized;
        @Email
        String email;
        @Pattern(regexp = "^[^@]*$")
        String pattern;
    }
}
