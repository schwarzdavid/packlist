package app.schwarzdavid.packlist.user.service.domain.user;

import app.schwarzdavid.packlist.user.service.domain.user.entity.UserEntity;
import app.schwarzdavid.packlist.user.service.domain.user.exception.UserAlreadyExistsException;
import app.schwarzdavid.packlist.user.service.domain.user.mapper.UserMapper;
import app.schwarzdavid.packlist.user.service.fixture.UserFixtures;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository, userMapper);
    }

    @Nested
    class CreateUser {
        @Test
        void shouldCreateUser() {
            when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
                var entity = invocation.getArgument(0, UserEntity.class);
                entity.setUserId(UserFixtures.USER_ID);
                return entity;
            });

            var user = userService.createUser(UserFixtures.userCreateData());

            var saveArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
            verify(userRepository).save(saveArgumentCaptor.capture());
            var userEntity = saveArgumentCaptor.getValue();

            assertEquals(UserFixtures.userData(), user);
            assertEquals(UserFixtures.USERNAME, userEntity.getUsername());
            assertEquals(UserFixtures.EMAIL, userEntity.getEmail());
            assertEquals(UserFixtures.HASHED_PASSWORD, userEntity.getPassword());
        }

        @Test
        void shouldThrowWhenUsernameAlreadyExists() {
            when(userRepository.save(any(UserEntity.class))).thenThrow(new DataIntegrityViolationException("", new ConstraintViolationException("", null, "", null, "uq_users_username")));

            assertThatThrownBy(() -> userService.createUser(UserFixtures.userCreateData())).isInstanceOf(UserAlreadyExistsException.class).extracting("field").isEqualTo(UserAlreadyExistsException.Field.USERNAME);
        }

        @Test
        void shouldThrowWhenEmailAlreadyExists() {
            when(userRepository.save(any(UserEntity.class))).thenThrow(new DataIntegrityViolationException("", new ConstraintViolationException("", null, "", null, "uq_users_email")));

            assertThatThrownBy(() -> userService.createUser(UserFixtures.userCreateData())).isInstanceOf(UserAlreadyExistsException.class).extracting("field").isEqualTo(UserAlreadyExistsException.Field.EMAIL);
        }

        @Test
        void shouldPipeExceptionWhenOtherExceptionOccurs() {
            when(userRepository.save(any(UserEntity.class))).thenThrow(new DataIntegrityViolationException("Unknown"));

            assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(UserFixtures.userCreateData()));
        }
    }

    @Nested
    class GetUserByIdentifier {
    }
}
