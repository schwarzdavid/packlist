package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.list.api.model.TemplateCreateDto;
import app.schwarzdavid.packlist.list.api.model.TemplateDto;
import app.schwarzdavid.packlist.list.api.model.TemplateItemDto;
import app.schwarzdavid.packlist.list.api.model.TemplatePreviewDto;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateItemEntity;
import app.schwarzdavid.packlist.list.service.fixture.ApiTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateControllerIT extends ApiTest {

    @Autowired
    private TemplateRepository templateRepository;

    @Nested
    public class CreateTemplate {
        private static final String URI = "/api/templates";

        private static Stream<Arguments> validCreateTemplateDtos() {
            return Stream.of(
                    Arguments.of(TemplateCreateDto.builder().name("foo").icon(null).build()),
                    Arguments.of(TemplateCreateDto.builder().name("bar").icon("icon").build()),
                    Arguments.of(TemplateCreateDto.builder().name("baz").icon("icon").items(null).build()),
                    Arguments.of(TemplateCreateDto.builder().name("baz").icon("icon").items(List.of("item-1", "item-2")).build())
            );
        }

        private static Stream<Arguments> invalidCreateTemplateDtos() {
            return Stream.of(
                    Arguments.of(new Object()),
                    Arguments.of(TemplateCreateDto.builder().name(null).build())
            );
        }

        @ParameterizedTest
        @MethodSource("validCreateTemplateDtos")
        void shouldCreateTemplate(TemplateCreateDto createTemplateDto) {
            var result = client.post().uri(URI).body(createTemplateDto)
                    .header("Authorization", "Bearer %s".formatted(getJwt()))
                    .exchange()
                    .expectStatus().isCreated()
                    .returnResult(TemplateDto.class)
                    .getResponseBody();

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(createTemplateDto.getName());
            assertThat(result.getIcon()).isEqualTo(createTemplateDto.getIcon());
            assertThat(result.getTemplateId()).isPositive();
            assertThat(result.getItems())
                    .isNotNull()
                    .allSatisfy(item -> {
                        assertThat(item).isNotNull();
                        assertThat(item.getTemplateItemId()).isPositive();
                        assertThat(item.getName()).isNotNull();
                    })
                    .extracting(TemplateItemDto::getName).containsExactlyElementsOf(createTemplateDto.getItems() == null ? List.of() : createTemplateDto.getItems());
        }

        @ParameterizedTest
        @MethodSource("invalidCreateTemplateDtos")
        void shouldRejectInvalidTemplates(Object body) {
            client.post().uri(URI).body(body)
                    .header("Authorization", "Bearer %s".formatted(getJwt()))
                    .exchange()
                    .expectStatus().isBadRequest();
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

    @Nested
    public class GetTemplatePreviews {
        private static final String URI = "/api/templates";

        @Test
        void shouldGetTemplatePreviews() {
            var templateEntity = TemplateEntity.builder().name("foo").icon("bar").createdBy(2L).items(new ArrayList<>()).build();
            for (int i = 1; i < 3; i++) {
                var item = TemplateItemEntity.builder().name("item-%d".formatted(i)).template(templateEntity).build();
                templateEntity.getItems().add(item);
            }
            templateRepository.save(templateEntity);

            var templates = client.get().uri(URI).header("Authorization", "Bearer %s".formatted(getJwt(2L))).exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<List<TemplatePreviewDto>>() {
            }).returnResult().getResponseBody();

            assertThat(templates).satisfiesExactly(template -> {
                assertThat(template.getTemplateId()).isPositive();
                assertThat(template.getName()).isEqualTo("foo");
                assertThat(template.getIcon()).isEqualTo("bar");
                assertThat(template.getItemsCount()).isEqualTo(2);
            });
        }

        @Test
        void shouldRejectUnauthenticatedRequests() {
            client.get().uri(URI).exchange().expectStatus().isUnauthorized();
        }

        @Test
        void shouldStillHaveIconSetIfNull() {
            templateRepository.save(TemplateEntity.builder().name("foo").icon(null).createdBy(2L).items(List.of()).build());

            var templates = client.get().uri(URI).header("Authorization", "Bearer %s".formatted(getJwt(2L))).exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<List<TemplatePreviewDto>>() {
            }).returnResult().getResponseBody();

            assertThat(templates).satisfiesExactly(template -> {
                assertThat(template.getTemplateId()).isPositive();
                assertThat(template.getName()).isEqualTo("foo");
                assertThat(template.getIcon()).isNull();
                assertThat(template.getItemsCount()).isEqualTo(0);
            });
        }

        @Test
        void shouldReturnEmptyListIfNoTemplatesExist() {
            var templates = client.get().uri(URI).header("Authorization", "Bearer %s".formatted(getJwt())).exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<List<TemplatePreviewDto>>() {
            }).returnResult().getResponseBody();

            assertThat(templates).isEmpty();
        }

        @Test
        void shouldIgnoreOtherUsersTemplates() {
            templateRepository.save(TemplateEntity.builder().name("foo").icon(null).createdBy(2L).items(List.of()).build());

            var templates = client.get().uri(URI).header("Authorization", "Bearer %s".formatted(getJwt())).exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<List<TemplatePreviewDto>>() {
            }).returnResult().getResponseBody();

            assertThat(templates).isEmpty();
        }
    }
}
