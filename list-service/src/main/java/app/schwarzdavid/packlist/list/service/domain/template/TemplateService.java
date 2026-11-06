package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.common.security.CurrentUserProvider;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateCreateData;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateData;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateItemEntity;
import app.schwarzdavid.packlist.list.service.domain.template.mapper.TemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateService {
    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;
    private final CurrentUserProvider currentUserProvider;

    public TemplateData createTemplate(TemplateCreateData templateCreateData) {
        var entity = TemplateEntity.builder()
                .name(templateCreateData.name())
                .icon(templateCreateData.icon())
                .createdBy(currentUserProvider.getUserId())
                .items(templateCreateData.items().stream().map(item -> TemplateItemEntity.builder()
                        .name(item)
                        .build()).collect(Collectors.toList()))
                .build();

        for (var item : entity.getItems()) {
            item.setTemplate(entity);
        }

        entity = templateRepository.save(entity);

        return templateMapper.toData(entity);
    }
}
