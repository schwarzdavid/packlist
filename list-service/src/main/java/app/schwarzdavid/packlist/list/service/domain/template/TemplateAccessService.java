package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.common.security.CurrentUserProvider;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import app.schwarzdavid.packlist.list.service.domain.template.exception.TemplateNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateAccessService {
    private final CurrentUserProvider currentUserProvider;
    private final TemplateRepository templateRepository;

    public TemplateEntity getOwnedTemplateEntity(Long templateId) {
        return templateRepository.findByTemplateIdAndCreatedBy(templateId, currentUserProvider.getUserId())
                .orElseThrow(TemplateNotFoundException::new);
    }
}
