package app.schwarzdavid.packlist.user.service.web;

import app.schwarzdavid.packlist.user.api.model.ProblemDetailsField;
import app.schwarzdavid.packlist.user.service.domain.user.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        var field = ProblemDetailsField.builder()
                .path(ex.getField().getPropertyName())
                .errors(List.of("validation.%sAlreadyExists".formatted(ex.getField().getPropertyName())))
                .build();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("fields", Map.of(ex.getField().getPropertyName(), field));
        return problemDetail;
    }
}
