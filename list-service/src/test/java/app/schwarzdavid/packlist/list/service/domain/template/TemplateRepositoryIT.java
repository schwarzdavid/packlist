package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.common.test.extension.DatabaseTruncateExtension;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateItemEntity;
import app.schwarzdavid.packlist.list.service.fixture.ListServiceTestcontainers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(DatabaseTruncateExtension.class)
@ImportTestcontainers(ListServiceTestcontainers.class)
public class TemplateRepositoryIT {
    @Autowired
    private TemplateRepository templateRepository;

    private TemplateEntity createTemplate(Long userId, String name, Integer itemCount) {
        var template = TemplateEntity.builder().name(name).icon(null).items(new ArrayList<>()).createdBy(userId).build();

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

            templateRepository.saveAllAndFlush(List.of(firstTemplate, secondTemplate));

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

        @Test
        void shouldReturnCountZeroWhenTemplateContainsNoItems() {
            templateRepository.saveAndFlush(createTemplate(1L, "first", 0));

            var previews = templateRepository.findAllPreviewsByUserId(1L);

            assertThat(previews).satisfiesExactly(preview -> assertThat(preview.itemsCount()).isEqualTo(0));
        }

        @Test
        void shouldIgnoreOtherUsersTemplates() {
            templateRepository.saveAllAndFlush(List.of(createTemplate(2L, "other", 3), createTemplate(1L, "own", 2)));

            var previews = templateRepository.findAllPreviewsByUserId(1L);

            assertThat(previews).satisfiesExactly(preview -> {
                assertThat(preview.name()).isEqualTo("own");
                assertThat(preview.itemsCount()).isEqualTo(2);
            });
        }
    }
}
