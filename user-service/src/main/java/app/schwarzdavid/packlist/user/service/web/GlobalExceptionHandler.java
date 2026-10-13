package app.schwarzdavid.packlist.user.service.web;

import app.schwarzdavid.packlist.user.api.model.ProblemDetailsField;
import app.schwarzdavid.packlist.user.service.domain.user.exception.UserAlreadyExistsException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
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
        var field = ProblemDetailsField.builder()
                .error("validation.%sTaken".formatted(ex.getField().getPropertyName()))
                .build();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("fields", Map.of(ex.getField().getPropertyName(), field));
        return problemDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setDetail("Invalid credentials");
        return problemDetail;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Unauthorized");
        return problemDetail;
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ProblemDetail handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Forbidden");
        return problemDetail;
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleAnyException(RuntimeException ex) {
        logger.error("Unexpected exception", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        return problemDetail;
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        var problemDetailsFields = new HashMap<String, ProblemDetailsField>();
        for (FieldError fieldError : ex.getFieldErrors()) {
            var problemDetailsField = ValidationErrorMapper.mapFieldError(fieldError);
            problemDetailsFields.put(fieldError.getField(), problemDetailsField);
        }
        ex.getBody().setProperty("fields", problemDetailsFields);
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }
}
