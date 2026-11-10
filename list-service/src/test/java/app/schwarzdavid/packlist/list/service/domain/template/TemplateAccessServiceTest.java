package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.common.security.CurrentUserProvider;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import app.schwarzdavid.packlist.list.service.domain.template.exception.TemplateNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TemplateAccessServiceTest {
    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private TemplateRepository templateRepository;

    @InjectMocks
    private TemplateAccessService templateAccessService;

    @Nested
    class GetOwnedTemplateEntity {
        @Test
        void shouldReturnTemplateEntity() {
            var templateEntity = new TemplateEntity();

            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(templateRepository.findByTemplateIdAndCreatedBy(1L, 1L)).thenReturn(Optional.of(templateEntity));

            var result = templateAccessService.getOwnedTemplateEntity(1L);

            assertThat(result).isSameAs(templateEntity);
        }

        @Test
        void shouldThrowExceptionWhenTemplateNotFound() {
            when(currentUserProvider.getUserId()).thenReturn(1L);
            when(templateRepository.findByTemplateIdAndCreatedBy(1L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> templateAccessService.getOwnedTemplateEntity(1L))
                    .isInstanceOf(TemplateNotFoundException.class);
        }

        @Test
        void shouldThrowExceptionWhenUserIsNotOwner() {
            when(currentUserProvider.getUserId()).thenReturn(2L);
            when(templateRepository.findByTemplateIdAndCreatedBy(1L, 2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> templateAccessService.getOwnedTemplateEntity(1L))
                    .isInstanceOf(TemplateNotFoundException.class);
        }
    }
}
