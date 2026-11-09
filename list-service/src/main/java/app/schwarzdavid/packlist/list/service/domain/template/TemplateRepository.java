package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.list.service.domain.template.data.TemplatePreviewData;
import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
    @Query("SELECT new app.schwarzdavid.packlist.list.service.domain.template.data.TemplatePreviewData(t.templateId, t.name, t.icon, COUNT(i)) FROM templates t LEFT JOIN t.items i WHERE t.createdBy = :userId GROUP BY t.templateId")
    List<TemplatePreviewData> findAllPreviewsByUserId(@Param("userId") Long userId);

    Optional<TemplateEntity> findByTemplateIdAndCreatedBy(@NotNull Long templateId, @NotNull Long createdBy);
}
