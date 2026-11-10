package app.schwarzdavid.packlist.list.service.web;

import app.schwarzdavid.packlist.list.service.domain.template.exception.TemplateNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TemplateNotFoundException.class)
    public ProblemDetail handleTemplateNotFoundException() {
        return ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    }
}
