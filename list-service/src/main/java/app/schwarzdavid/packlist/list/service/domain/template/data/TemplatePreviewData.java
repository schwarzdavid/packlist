package app.schwarzdavid.packlist.list.service.domain.template.data;

import org.jspecify.annotations.Nullable;

public record TemplatePreviewData(
        Long templateId,
        String name,
        @Nullable String icon,
        Long itemsCount
) {
}
