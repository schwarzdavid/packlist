package app.schwarzdavid.packlist.list.service.domain.template.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity(name = "templates")
public class TemplateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id", updatable = false)
    private Long templateId;

    private String name;

    private String icon;

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
    private Set<TemplateItemEntity> items;
}
