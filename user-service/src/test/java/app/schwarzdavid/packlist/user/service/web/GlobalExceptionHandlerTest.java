package app.schwarzdavid.packlist.user.service.web;

import app.schwarzdavid.packlist.common.web.dto.ProblemDetailsFieldDto;
import app.schwarzdavid.packlist.user.service.domain.user.exception.UserAlreadyExistsException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setup() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldMapUserAlreadyExistsException() {
        var exception = new UserAlreadyExistsException(UserAlreadyExistsException.Field.EMAIL);
        var problem = globalExceptionHandler.handleUserAlreadyExistsException(exception);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getProperties()).isNotNull()
                .extractingByKey("fields", InstanceOfAssertFactories.map(String.class, ProblemDetailsFieldDto.class))
                .containsOnlyKeys("email")
                .extractingByKey("email")
                .satisfies(field -> {
                    assertThat(field.getError()).isEqualTo("validation.emailTaken");
                });
    }
}
