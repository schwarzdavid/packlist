package app.schwarzdavid.packlist.list.service.domain.template.mapper;

import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateItemData;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateItemEntity;
import org.mapstruct.Mapper;

@Mapper
public interface TemplateItemMapper {
    TemplateItemData toData(TemplateItemEntity templateItemEntity);
}
