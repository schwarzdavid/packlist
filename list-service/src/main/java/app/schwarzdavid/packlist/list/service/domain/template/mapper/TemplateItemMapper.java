package app.schwarzdavid.packlist.list.service.domain.template.mapper;

import app.schwarzdavid.packlist.list.api.model.TemplateItemEditDto;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateItemEditData;
import org.mapstruct.Mapper;

@Mapper
public interface TemplateItemMapper {
    TemplateItemEditData toData(TemplateItemEditDto templateItemEditDto);
}
