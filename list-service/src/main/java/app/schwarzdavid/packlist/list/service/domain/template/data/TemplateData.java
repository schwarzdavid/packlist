package app.schwarzdavid.packlist.list.service.domain.template.data;

import org.jspecify.annotations.Nullable;

import java.util.Set;

public record TemplateData(
        Long templateId,
        String name,
        @Nullable String icon,
        Set<TemplateTagData> inherits,
        Set<TemplateItemData> items
) {
}
