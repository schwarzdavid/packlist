package app.schwarzdavid.packlist.list.service.domain.template;

import app.schwarzdavid.packlist.list.service.domain.template.entity.TemplateItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateItemRepository extends JpaRepository<TemplateItemEntity, Long> {
}
