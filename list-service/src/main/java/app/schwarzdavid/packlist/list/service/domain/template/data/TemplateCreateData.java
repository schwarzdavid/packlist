package app.schwarzdavid.packlist.list.service.domain.template.data;

import org.jspecify.annotations.Nullable;

import java.util.List;

public record TemplateCreateData(
        String name,
        @Nullable String icon,
        List<String> items,
        Long createdBy
) {
    public TemplateCreateData {
        items = items == null ? List.of() : items;
    }
}
