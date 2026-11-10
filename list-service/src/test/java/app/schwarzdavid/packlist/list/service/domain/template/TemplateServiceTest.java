package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.common.security.CurrentUserProvider;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateCreateData;
import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateData;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import app.schwarzdavid.packlist.list.service.domain.template.mapper.TemplateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TemplateServiceTest {
    private final TemplateMapper templateMapper = Mappers.getMapper(TemplateMapper.class);

    @Mock
    private TemplateAccessServiceTest templateAccessService;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private TemplateRepository templateRepository;

    private TemplateService templateService;

    @BeforeEach
    void setup() {
        templateService = new TemplateService(templateRepository, templateMapper, templateAccessService, currentUserProvider);
    }

    @Test
    void shouldCreateTemplate() {
        var createData = new TemplateCreateData("foo", null, List.of());

        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(templateRepository.save(any(TemplateEntity.class))).then(answer -> {
            var entity = answer.getArgument(0, TemplateEntity.class);
            entity.setTemplateId(1L);
            return entity;
        });

        var result = templateService.createTemplate(createData);

        assertThat(result).isInstanceOf(TemplateData.class);
        assertThat(result.name()).isEqualTo("foo");
        assertThat(result.templateId()).isEqualTo(1L);
        assertThat(result.icon()).isNull();
        assertThat(result.createdBy()).isEqualTo(1L);
        assertThat(result.items().size()).isEqualTo(0);
    }

    @Test
    void shouldCreateTemplateWithIcon() {
        var createData = new TemplateCreateData("foo", "mdi-account", List.of());

        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(templateRepository.save(any(TemplateEntity.class))).then(answer -> {
            var entity = answer.getArgument(0, TemplateEntity.class);
            entity.setTemplateId(1L);
            return entity;
        });

        var result = templateService.createTemplate(createData);
        assertThat(result).isInstanceOf(TemplateData.class);
        assertThat(result.name()).isEqualTo("foo");
        assertThat(result.templateId()).isEqualTo(1L);
        assertThat(result.icon()).isEqualTo("mdi-account");
        assertThat(result.createdBy()).isEqualTo(1L);
        assertThat(result.items().size()).isEqualTo(0);
    }

    @Test
    void shouldCreateTemplateWithItems() {
        var createData = new TemplateCreateData(
                "foo",
                "icon",
                List.of("item1", "item2")
        );

        when(currentUserProvider.getUserId()).thenReturn(1L);
        when(templateRepository.save(any(TemplateEntity.class))).then(answer -> {
            var entity = answer.getArgument(0, TemplateEntity.class);
            entity.setTemplateId(1L);
            var itemIndex = 0L;
            for (var item : entity.getItems()) {
                item.setTemplateItemId(++itemIndex);
            }
            return entity;
        });

        var result = templateService.createTemplate(createData);

        assertThat(result).isInstanceOf(TemplateData.class);
        assertThat(result.items()).satisfiesExactly(
                item -> {
                    assertThat(item.templateItemId()).isEqualTo(1L);
                    assertThat(item.name()).isEqualTo("item1");
                },
                item -> {
                    assertThat(item.templateItemId()).isEqualTo(2L);
                    assertThat(item.name()).isEqualTo("item2");
                }
        );
    }
}
