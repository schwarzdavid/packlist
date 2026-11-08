package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.common.test.extension.DatabaseTruncateExtension;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateItemEntity;
import app.schwarzdavid.packlist.list.service.fixture.ListServiceTestcontainers;
import jakarta.persistence.EntityManager;
import kotlin.collections.ArrayDeque;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(DatabaseTruncateExtension.class)
@ImportTestcontainers(ListServiceTestcontainers.class)
public class TemplateRepositoryIT {
    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private EntityManager entityManager;

    private TemplateEntity createTemplate(Long userId, String name, Integer itemCount) {
        var template = TemplateEntity.builder().name(name).icon(null).items(new ArrayDeque<>()).createdBy(userId).build();

        for (int i = 0; i < itemCount; i++) {
            var item = TemplateItemEntity.builder().name("item-%d".formatted(i)).template(template).build();
            template.getItems().add(item);
        }

        return template;
    }

    @Nested
    class findAllPreviewsByUser {
        @Test
        void shouldReturnAllPreviewsWithItsCount() {
            var firstTemplate = createTemplate(1L, "first", 2);
            var secondTemplate = createTemplate(1L, "second", 4);
            var third = createTemplate(2L, "third", 5);

            templateRepository.saveAll(List.of(firstTemplate, secondTemplate, third));
            entityManager.flush();
            entityManager.clear();

            var previews = templateRepository.findAllPreviewsByUserId(1L);

            assertThat(previews).satisfiesExactlyInAnyOrder(
                    preview -> {
                        assertThat(preview.name()).isEqualTo("first");
                        assertThat(preview.itemsCount()).isEqualTo(2);
                    },
                    preview -> {
                        assertThat(preview.name()).isEqualTo("second");
                        assertThat(preview.itemsCount()).isEqualTo(4);
                    }
            );
        }
    }
}
