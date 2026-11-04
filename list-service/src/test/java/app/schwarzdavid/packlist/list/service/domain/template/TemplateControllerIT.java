package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateCreateData;
import app.schwarzdavid.packlist.list.service.fixture.ApiTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateControllerIT extends ApiTest {

    @Nested
    public class CreateTemplate {
        private static final String URI = "/api/templates";

        @Test
        void shouldCreateTemplate() {
            var newTemplate = new TemplateCreateData("foo", null, List.of(), List.of());

            var result = client.post().uri(URI).body(newTemplate)
                    .header("Authorization", "Bearer %s".formatted(getJwt()))
                    .exchange()
                    .expectStatus().isCreated()
                    .returnResult();

            assertThat(result).isNotNull();
        }
    }
}
