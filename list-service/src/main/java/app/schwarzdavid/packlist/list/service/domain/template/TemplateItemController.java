package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.list.api.TemplateItemApi;
import app.schwarzdavid.packlist.list.api.model.TemplateDto;
import app.schwarzdavid.packlist.list.api.model.TemplateItemEditDto;
import app.schwarzdavid.packlist.list.service.domain.template.mapper.TemplateItemMapper;
import app.schwarzdavid.packlist.list.service.domain.template.mapper.TemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TemplateItemController implements TemplateItemApi {
    private final TemplateItemMapper templateItemMapper;
    private final TemplateItemService templateItemService;
    private final TemplateMapper templateMapper;

    @Override
    public ResponseEntity<TemplateDto> addTemplateItem(Long templateId, TemplateItemEditDto templateItemEditDto) {
        var templateItemData = templateItemMapper.toData(templateItemEditDto);
        var templateData = templateItemService.addTemplateItem(templateId, templateItemData);
        var templateDto = templateMapper.toDto(templateData);

        return ResponseEntity.status(HttpStatus.CREATED).body(templateDto);
    }
}
