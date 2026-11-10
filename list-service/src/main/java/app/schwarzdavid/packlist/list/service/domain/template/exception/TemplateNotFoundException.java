package app.schwarzdavid.packlist.list.service.domain.template.exception;

public class TemplateNotFoundException extends RuntimeException {
    public TemplateNotFoundException() {
        super("Template not found");
    }
}
