package app.schwarzdavid.packlist.list.service.domain.template.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "template_items")
public class TemplateItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_item_id")
    private Long templateItemId;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private TemplateEntity template;
}
