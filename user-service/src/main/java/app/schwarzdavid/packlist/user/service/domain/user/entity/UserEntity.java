package app.schwarzdavid.packlist.user.service.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(name = "uq_users_username", columnNames = "username"), @UniqueConstraint(name = "uq_users_email", columnNames = "email")})
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Getter
    private Long userId;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String password;
}
