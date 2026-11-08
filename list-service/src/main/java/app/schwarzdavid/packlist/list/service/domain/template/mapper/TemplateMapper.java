package app.schwarzdavid.packlist.list.service.domain.template.mapper;

import app.schwarzdavid.packlist.list.api.model.TemplateCreateDto;
import app.schwarzdavid.packlist.list.api.model.TemplateDto;
import app.schwarzdavid.packlist.list.api.model.TemplatePreviewDto;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateCreateData;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateData;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplatePreviewData;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface TemplateMapper {
    @Mapping(target = "createdBy", source = "userId")
    TemplateCreateData toData(TemplateCreateDto templateCreateDto, Long userId);

    TemplateData toData(TemplateEntity templateEntity);

    List<TemplatePreviewDto> toDto(List<TemplatePreviewData> templatePreviewDatas);

    TemplateDto toDto(TemplateData templateData);
}
