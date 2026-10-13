package app.schwarzdavid.packlist.user.service.domain.user;

import app.schwarzdavid.packlist.user.service.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

    default Optional<UserEntity> findByIdentifier(String identifyer) {
        return findByUsernameIgnoreCaseOrEmailIgnoreCase(identifyer, identifyer);
    }
}
