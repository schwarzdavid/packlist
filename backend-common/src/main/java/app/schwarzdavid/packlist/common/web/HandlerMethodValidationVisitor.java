package app.schwarzdavid.packlist.common.web;

import app.schwarzdavid.packlist.common.web.dto.ProblemDetailsFieldDto;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class HandlerMethodValidationVisitor implements HandlerMethodValidationException.Visitor {
    @Getter
    private final Map<String, ProblemDetailsFieldDto> fieldsMap = new HashMap<>();

    @Getter
    private final Map<String, ProblemDetailsFieldDto> paramsMap = new HashMap<>();

    @Override
    public void cookieValue(CookieValue cookieValue, ParameterValidationResult result) {
        // ignore
    }

    @Override
    public void matrixVariable(MatrixVariable matrixVariable, ParameterValidationResult result) {
        // ignore
    }

    @Override
    public void modelAttribute(@Nullable ModelAttribute modelAttribute, ParameterErrors errors) {
        // ignore
    }

    @Override
    public void pathVariable(PathVariable pathVariable, ParameterValidationResult result) {
        var constraintViolation = result.unwrap(result.getResolvableErrors().getFirst(), ConstraintViolation.class);
        paramsMap.put(pathVariable.name(), ValidationErrorMapper.mapConstraintViolation(constraintViolation));
    }

    @Override
    public void requestBody(RequestBody requestBody, ParameterErrors errors) {
        for (var fieldError : errors.getFieldErrors()) {
            fieldsMap.put(fieldError.getField(), ValidationErrorMapper.mapFieldError(fieldError));
        }
    }

    @Override
    public void requestHeader(RequestHeader requestHeader, ParameterValidationResult result) {
        // ignore
    }

    @Override
    public void requestParam(@Nullable RequestParam requestParam, ParameterValidationResult result) {
        if (requestParam == null) {
            return;
        }
        var constraintViolation = result.unwrap(result.getResolvableErrors().getFirst(), ConstraintViolation.class);
        paramsMap.put(requestParam.name(), ValidationErrorMapper.mapConstraintViolation(constraintViolation));
    }

    @Override
    public void requestPart(RequestPart requestPart, ParameterErrors errors) {
        // ignore
    }

    @Override
    public void other(ParameterValidationResult result) {
        // ignore
    }
}
