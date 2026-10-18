package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.list.api.TemplateApi;
import app.schwarzdavid.packlist.list.api.model.TemplateCreateDto;
import app.schwarzdavid.packlist.list.api.model.TemplatePreviewDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateController implements TemplateApi {
    @Override
    public ResponseEntity<TemplatePreviewDto> createTemplate(TemplateCreateDto templateCreateDto) {
        return null;
    }
}
