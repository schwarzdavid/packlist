package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.list.service.domain.template.data.TemplateItemEditData;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateItemEntity;
import app.schwarzdavid.packlist.list.service.domain.template.exception.TemplateNotFoundException;
import app.schwarzdavid.packlist.list.service.domain.template.mapper.TemplateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TemplateItemServiceTest {
    private final TemplateMapper templateMapper = Mappers.getMapper(TemplateMapper.class);

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private TemplateAccessService templateAccessService;

    private TemplateItemService templateItemService;

    @BeforeEach
    void setup() {
        templateItemService = new TemplateItemService(templateRepository, templateAccessService, templateMapper);
    }

    @Nested
    class AddTemplateItem {
        @Test
        void shouldAddItemToTemplate() {
            var itemEditData = new TemplateItemEditData("item");
            var templateEntity = TemplateEntity.builder().templateId(1L).name("template").items(new ArrayList<>()).createdBy(1L).build();

            when(templateAccessService.getOwnedTemplateEntity(1L)).thenReturn(templateEntity);
            when(templateRepository.save(any(TemplateEntity.class))).thenAnswer(invocation -> invocation.getArgument(0, TemplateEntity.class));

            var result = templateItemService.addTemplateItem(1L, itemEditData);
            var entityCaptor = ArgumentCaptor.forClass(TemplateEntity.class);

            verify(templateRepository).save(entityCaptor.capture());
            assertThat(entityCaptor.getValue().getItems()).allSatisfy(item -> assertThat(item.getTemplate()).isEqualTo(templateEntity));
            assertThat(result.items()).satisfiesExactly(item -> assertThat(item.name()).isEqualTo("item"));
        }

        @Test
        void shouldNotSaveItemIfTemplateDoesNotExist() {
            var itemEditData = new TemplateItemEditData("item");

            when(templateAccessService.getOwnedTemplateEntity(1L)).thenThrow(new TemplateNotFoundException());

            assertThatThrownBy(() -> templateItemService.addTemplateItem(1L, itemEditData)).isInstanceOf(TemplateNotFoundException.class);
            verifyNoInteractions(templateRepository);
        }

        @Test
        void shouldAddItemToTemplateWithExistingItems() {
            var itemEditData = new TemplateItemEditData("new-item");

            var templateEntity = TemplateEntity.builder().templateId(1L).name("template").items(new ArrayList<>()).createdBy(1L).build();
            var templateItem = TemplateItemEntity.builder().name("existing-item").templateItemId(1L).template(templateEntity).build();

            templateEntity.getItems().add(templateItem);
            when(templateAccessService.getOwnedTemplateEntity(1L)).thenReturn(templateEntity);
            when(templateRepository.save(any(TemplateEntity.class))).thenAnswer(invocation -> invocation.getArgument(0, TemplateEntity.class));

            var result = templateItemService.addTemplateItem(1L, itemEditData);
            var entityCaptor = ArgumentCaptor.forClass(TemplateEntity.class);

            verify(templateRepository).save(entityCaptor.capture());
            assertThat(entityCaptor.getValue().getItems()).satisfiesExactly(
                    item -> assertThat(item.getName()).isEqualTo("existing-item"),
                    item -> assertThat(item.getName()).isEqualTo("new-item")
            );
            assertThat(result.items()).satisfiesExactly(
                    item -> assertThat(item.name()).isEqualTo("existing-item"),
                    item -> assertThat(item.name()).isEqualTo("new-item")
            );
        }
    }
}
