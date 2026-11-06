package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.list.api.model.TemplateCreateDto;
import app.schwarzdavid.packlist.list.api.model.TemplateDto;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateCreateData;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import app.schwarzdavid.packlist.list.service.fixture.ApiTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateControllerIT extends ApiTest {

    @Autowired
    private TemplateRepository templateRepository;

    @Nested
    public class CreateTemplate {
        private static final String URI = "/api/templates";

        @Test
        void shouldCreateTemplate() {
            // TODO: make it parametrized
            var newTemplate = new TemplateCreateData("foo", null, List.of());

            var result = client.post().uri(URI).body(newTemplate)
                    .header("Authorization", "Bearer %s".formatted(getJwt()))
                    .exchange()
                    .expectStatus().isCreated()
                    .returnResult();

            assertThat(result).isNotNull();
        }

        @Test
        void shouldRejectUnauthenticatedRequests() {
            var newTemplate = TemplateCreateDto.builder().name("foo").build();

            client.post().uri(URI).body(newTemplate).exchange().expectStatus().isUnauthorized();
        }

        @Test
        void shouldCreateTemplateIfTemplateWithSameNameAlreadyExists() {
            var existingTemplate = templateRepository.save(new TemplateEntity(null, "foo", "icon", 1L, null));
            var templateCreateDto = TemplateCreateDto.builder().name("foo").build();

            var result = client.post().uri(URI).body(templateCreateDto)
                    .header("Authorization", "Bearer %s".formatted(getJwt()))
                    .exchange()
                    .expectStatus().isCreated()
                    .returnResult(TemplateDto.class)
                    .getResponseBody();

            assertThat(result).isNotNull();
            assertThat(result.getTemplateId()).isNotEqualTo(existingTemplate.getTemplateId());
            assertThat(result.getName()).isEqualTo("foo");
            assertThat(result.getIcon()).isEqualTo(null);
        }
    }
}
