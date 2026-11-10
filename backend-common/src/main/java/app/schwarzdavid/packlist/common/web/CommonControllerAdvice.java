package app.schwarzdavid.packlist.common.web;

import app.schwarzdavid.packlist.common.web.dto.ProblemDetailsFieldDto;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;

@RestControllerAdvice
public class CommonControllerAdvice extends ResponseEntityExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(CommonControllerAdvice.class);

    @Order()
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleAnyException(RuntimeException ex) {
        logger.error("Unexpected exception", ex);

        var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        return problemDetail;
    }

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

    @Override
    protected @Nullable ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        var problemDetailsFields = new HashMap<String, ProblemDetailsFieldDto>();
        for (var result : ex.getParameterValidationResults()) {
            problemDetailsFields.put((String) result.getContainerKey(), null);
        }
        ex.getBody().setProperty("fields", problemDetailsFields);
        return super.handleHandlerMethodValidationException(ex, headers, status, request);
    }
}
