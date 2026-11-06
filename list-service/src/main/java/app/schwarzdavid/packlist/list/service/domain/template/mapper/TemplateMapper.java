package app.schwarzdavid.packlist.list.service.domain.template.mapper;

import app.schwarzdavid.packlist.list.api.model.TemplateCreateDto;
import app.schwarzdavid.packlist.list.api.model.TemplateDto;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateCreateData;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateData;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import org.mapstruct.Mapper;

@Mapper
public interface TemplateMapper {
    TemplateCreateData toData(TemplateCreateDto templateCreateDto);

    TemplateData toData(TemplateEntity templateEntity);

    TemplateDto toDto(TemplateData templateData);
}
