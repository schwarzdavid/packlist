package app.schwarzdavid.packlist.list.service.domain.template.data;

import org.jspecify.annotations.Nullable;

import java.util.List;

public record TemplateData(
        Long templateId,
        String name,
        @Nullable String icon,
        Long createdBy,
        List<TemplateItemData> items
) {
}
