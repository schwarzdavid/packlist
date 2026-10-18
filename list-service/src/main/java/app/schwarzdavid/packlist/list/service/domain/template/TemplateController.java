package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.list.api.TemplateApi;
import app.schwarzdavid.packlist.list.api.model.TemplateCreateDto;
import app.schwarzdavid.packlist.list.api.model.TemplateDto;
import app.schwarzdavid.packlist.list.service.domain.template.mapper.TemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TemplateController implements TemplateApi {
    private final TemplateMapper templateMapper;
    private final TemplateService templateService;

    @Override
    public ResponseEntity<TemplateDto> createTemplate(TemplateCreateDto templateCreateDto) {
        var createData = templateMapper.toData(templateCreateDto);
        var template = templateService.createTemplate(createData);
        var templateDto = templateMapper.toDto(template);

        return ResponseEntity.status(HttpStatus.CREATED).body(templateDto);
    }
}
