package app.schwarzdavid.packlist.user.service.web;

import app.schwarzdavid.packlist.user.api.model.ProblemDetailsFieldDto;
import app.schwarzdavid.packlist.user.service.domain.user.exception.UserAlreadyExistsException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        var field = ProblemDetailsFieldDto.builder()
                .error("validation.%sTaken".formatted(ex.getField().getPropertyName()))
                .build();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("fields", Map.of(ex.getField().getPropertyName(), field));
        return problemDetail;
    }

    // TODO: move into backend-common
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleAnyException(RuntimeException ex) {
        logger.error("Unexpected exception", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        return problemDetail;
    }

    // TODO: move into backend-common
    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        var problemDetailsFields = new HashMap<String, ProblemDetailsFieldDto>();
        for (FieldError fieldError : ex.getFieldErrors()) {
            var problemDetailsField = ValidationErrorMapper.mapFieldError(fieldError);
            problemDetailsFields.put(fieldError.getField(), problemDetailsField);
        }
        ex.getBody().setProperty("fields", problemDetailsFields);
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }
}
