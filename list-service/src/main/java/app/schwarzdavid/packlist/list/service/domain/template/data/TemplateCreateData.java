package app.schwarzdavid.packlist.list.service.domain.template.data;

import org.jspecify.annotations.Nullable;

import java.util.List;

public record TemplateCreateData(
        String name,
        @Nullable String icon,
        List<Long> inherits,
        List<String> items
) {
}
