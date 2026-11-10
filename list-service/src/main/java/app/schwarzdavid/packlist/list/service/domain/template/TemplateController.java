package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.common.security.CurrentUserProvider;
import app.schwarzdavid.packlist.list.api.TemplateApi;
import app.schwarzdavid.packlist.list.api.model.TemplateCreateDto;
import app.schwarzdavid.packlist.list.api.model.TemplateDto;
import app.schwarzdavid.packlist.list.api.model.TemplatePreviewDto;
import app.schwarzdavid.packlist.list.service.domain.template.mapper.TemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TemplateController implements TemplateApi {
    private final TemplateMapper templateMapper;
    private final TemplateService templateService;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public ResponseEntity<TemplateDto> createTemplate(TemplateCreateDto templateCreateDto) {
        var createData = templateMapper.toData(templateCreateDto, currentUserProvider.getUserId());
        var template = templateService.createTemplate(createData);
        var templateDto = templateMapper.toDto(template);

        return ResponseEntity.status(HttpStatus.CREATED).body(templateDto);
    }

    @Override
    public ResponseEntity<TemplateDto> getTemplate(Long templateId) {
        var templateData = templateService.getTemplateForUser(templateId);
        var templateDto = templateMapper.toDto(templateData);

        return ResponseEntity.ok(templateDto);
    }

    @Override
    public ResponseEntity<List<TemplatePreviewDto>> getTemplates() {
        var userId = currentUserProvider.getUserId();
        var templatePreviews = templateService.getTemplatePreviewsForUser(userId);
        var templatePreviewDtos = templateMapper.toDto(templatePreviews);

        return ResponseEntity.ok(templatePreviewDtos);
    }
}
