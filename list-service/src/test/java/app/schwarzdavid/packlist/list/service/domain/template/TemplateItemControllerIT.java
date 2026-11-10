package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.list.api.model.TemplateDto;
import app.schwarzdavid.packlist.list.api.model.TemplateItemEditDto;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateItemEntity;
import app.schwarzdavid.packlist.list.service.fixture.ApiTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TemplateItemControllerIT extends ApiTest {
    @Autowired
    private TemplateRepository templateRepository;

    @Nested
    class AddTemplateItem {
        private static final String URI = "/api/templates/{templateId}/items";

        private static Stream<Arguments> invalidItemDtos() {
            return Stream.of(
                    Arguments.of(TemplateItemEditDto.builder().build()),
                    Arguments.of(TemplateItemEditDto.builder().name("").build()),
                    Arguments.of(TemplateItemEditDto.builder().name("x".repeat(300)).build()),
                    Arguments.of(new Object())
            );
        }

        private static Stream<Arguments> invalidTemplateIds() {
            return Stream.of(
                    Arguments.of("foo"),
                    Arguments.of(0),
                    Arguments.of(-1)
            );
        }

        @Test
        void shouldAddItemToExistingTemplate() {
            var savedTemplate = templateRepository.save(TemplateEntity.builder().name("foo").createdBy(1L).build());
            var editItemDto = TemplateItemEditDto.builder().name("item").build();

            var result = client.post().uri(URI, savedTemplate.getTemplateId())
                    .body(editItemDto)
                    .header("Authorization", "Bearer %s".formatted(getJwt(1L)))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(TemplateDto.class)
                    .returnResult().getResponseBody();

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("foo");
            assertThat(result.getIcon()).isNull();
            assertThat(result.getItems()).satisfiesExactly(item -> {
                assertThat(item.getTemplateItemId()).isPositive();
                assertThat(item.getName()).isEqualTo("item");
            });

            var updatedTemplate = templateRepository.findByTemplateIdAndCreatedBy(savedTemplate.getTemplateId(), 1L).orElseThrow();

            assertThat(updatedTemplate.getItems()).satisfiesExactly(
                    item -> {
                        assertThat(item.getTemplateItemId()).isPositive();
                        assertThat(item.getName()).isEqualTo("item");
                    }
            );
        }

        @Test
        void shouldAddItemToExistingTemplateWithMultipleItems() {
            var template = TemplateEntity.builder().name("foo").createdBy(1L).items(new ArrayList<>()).build();
            var templateItem = TemplateItemEntity.builder().name("item-1").template(template).build();
            template.getItems().add(templateItem);
            template = templateRepository.save(template);

            var itemAddDto = TemplateItemEditDto.builder().name("item-2").build();

            var result = client.post().uri(URI, template.getTemplateId())
                    .body(itemAddDto)
                    .header("Authorization", "Bearer %s".formatted(getJwt(1L)))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(TemplateDto.class)
                    .returnResult().getResponseBody();

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("foo");
            assertThat(result.getIcon()).isNull();
            assertThat(result.getItems()).satisfiesExactly(
                    item -> {
                        assertThat(item.getTemplateItemId()).isEqualTo(templateItem.getTemplateItemId());
                        assertThat(item.getName()).isEqualTo("item-1");
                    },
                    item -> {
                        assertThat(item.getTemplateItemId()).isPositive();
                        assertThat(item.getName()).isEqualTo("item-2");
                    }
            );

            var updatedTemplate = templateRepository.findByTemplateIdAndCreatedBy(template.getTemplateId(), 1L).orElseThrow();

            assertThat(updatedTemplate.getItems()).satisfiesExactly(
                    item -> {
                        assertThat(item.getTemplateItemId()).isEqualTo(templateItem.getTemplateItemId());
                        assertThat(item.getName()).isEqualTo("item-1");
                    },
                    item -> {
                        assertThat(item.getTemplateItemId()).isPositive();
                        assertThat(item.getName()).isEqualTo("item-2");
                    }
            );
        }

        @Test
        void shouldThrowWhenTemplateDoesNotExist() {
            var itemAddDto = TemplateItemEditDto.builder().name("item").build();

            client.post().uri(URI, 1L)
                    .body(itemAddDto)
                    .header("Authorization", "Bearer %s".formatted(getJwt(1L)))
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void shouldThrowWhenTemplateBelongsToOtherUser() {
            var template = templateRepository.save(TemplateEntity.builder().name("foo").createdBy(1L).build());
            var itemAddDto = TemplateItemEditDto.builder().name("item").build();

            client.post().uri(URI, template.getTemplateId())
                    .body(itemAddDto)
                    .header("Authorization", "Bearer %s".formatted(getJwt(2L)))
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void shouldThrowWhenUnauthorized() {
            client.post().uri(URI, 1L)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }

        @ParameterizedTest
        @MethodSource("invalidItemDtos")
        void shouldRejectInvalidRequestBodies(Object body) {
            client.post().uri(URI, 1L)
                    .body(body)
                    .header("Authorization", "Bearer %s".formatted(getJwt(1L)))
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @ParameterizedTest
        @MethodSource("invalidTemplateIds")
        void shouldRejectInvalidPathParam(Object templateId) {
            var itemAddDto = TemplateItemEditDto.builder().name("item").build();

            client.post().uri(URI, templateId)
                    .body(itemAddDto)
                    .header("Authorization", "Bearer %s".formatted(getJwt()))
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }
}
