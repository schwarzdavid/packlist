package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateData;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateItemEditData;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateItemEntity;
import app.schwarzdavid.packlist.list.service.domain.template.mapper.TemplateMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateItemService {
    private final TemplateRepository templateRepository;
    private final TemplateAccessService templateAccessService;
    private final TemplateMapper templateMapper;

    @Transactional
    public TemplateData addTemplateItem(Long templateId, TemplateItemEditData templateItemEditData) {
        var templateEntity = templateAccessService.getOwnedTemplateEntity(templateId);
        var templateItemEntity = TemplateItemEntity.builder().name(templateItemEditData.name()).template(templateEntity).build();

        templateEntity.getItems().add(templateItemEntity);
        templateEntity = templateRepository.save(templateEntity);

        return templateMapper.toData(templateEntity);
    }
}
