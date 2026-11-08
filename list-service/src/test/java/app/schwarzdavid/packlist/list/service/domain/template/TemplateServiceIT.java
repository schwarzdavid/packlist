package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.common.test.security.WithJwtUser;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateCreateData;
import app.schwarzdavid.packlist.list.service.fixture.ListServiceIntegrationTest;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ListServiceIntegrationTest
public class TemplateServiceIT {
    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    class CreateTemplate {
        @Test
        @WithJwtUser(20L)
        @Transactional
        void shouldCreateTemplateWithItems() {
            var templateCreateData = new TemplateCreateData("foo", "bar", List.of("item-1", "item-2"), 20L);

            var result = templateService.createTemplate(templateCreateData);

            assertThat(result).isNotNull();
            assertThat(result.templateId()).isNotNull();
            assertThat(result.name()).isEqualTo("foo");
            assertThat(result.icon()).isEqualTo("bar");
            assertThat(result.createdBy()).isEqualTo(20L);
            assertThat(result.items()).hasSize(2);

            assertThat(result.items()).satisfiesExactlyInAnyOrder(
                    item -> {
                        assertThat(item.templateItemId()).isNotNull();
                        assertThat(item.name()).isEqualTo("item-1");
                    }, item -> {
                        assertThat(item.templateItemId()).isNotNull();
                        assertThat(item.name()).isEqualTo("item-2");
                    }
            );

            entityManager.flush();
            entityManager.clear();


            var templateEntityOptional = templateRepository.findById(result.templateId());

            assertThat(templateEntityOptional).isPresent();

            var templateEntity = templateEntityOptional.get();

            assertThat(templateEntity).isNotNull();
            assertThat(templateEntity.getName()).isEqualTo("foo");
            assertThat(templateEntity.getIcon()).isEqualTo("bar");
            assertThat(templateEntity.getCreatedBy()).isEqualTo(20L);
            assertThat(templateEntity.getItems()).hasSize(2);
        }

        @Test
        @Transactional
        @WithJwtUser
        void shouldCreateTemplateWithoutItems() {
            var templateCreate = new TemplateCreateData("foo", null, List.of(), 1L);

            var result = templateService.createTemplate(templateCreate);

            assertThat(result).isNotNull();
            assertThat(result.items()).isEmpty();

            entityManager.flush();
            entityManager.clear();

            var templateOptional = templateRepository.findById(result.templateId());
            assertThat(templateOptional).isPresent();
            var template = templateOptional.get();
            assertThat(template.getItems()).isEmpty();
        }
    }
}
