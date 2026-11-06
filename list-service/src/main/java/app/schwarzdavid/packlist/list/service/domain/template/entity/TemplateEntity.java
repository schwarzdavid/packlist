package app.schwarzdavid.packlist.list.service.domain.template.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "templates")
public class TemplateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id", updatable = false)
    private Long templateId;

    private String name;

    private String icon;

    @Column(name = "created_by")
    private Long createdBy;

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<TemplateItemEntity> items;
}
