package app.schwarzdavid.packlist.user.service.domain.user;

import app.schwarzdavid.packlist.user.service.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import app.schwarzdavid.packlist.user.service.domain.user.exception.UserAlreadyExistsException;
import app.schwarzdavid.packlist.user.service.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Map<String, UserAlreadyExistsException.Field> CONSTRAINT_FIELD_MAP = Map.of(
            "uq_users_username", UserAlreadyExistsException.Field.USERNAME,
            "uq_users_email", UserAlreadyExistsException.Field.EMAIL
    );

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserData createUser(UserCreateData userCreateData) {
        var entity = userMapper.toNewUserEntity(userCreateData);
        try {
            entity = userRepository.save(entity);
        } catch (DataIntegrityViolationException exception) {
            throw getMappedDataIntegrityViolationException(exception);
        }
        return userMapper.toUserData(entity);
    }

    private RuntimeException getMappedDataIntegrityViolationException(DataIntegrityViolationException exception) {
        if (exception.getCause() instanceof ConstraintViolationException constraintViolationException) {
            var field = CONSTRAINT_FIELD_MAP.get(constraintViolationException.getConstraintName());
            if (field != null) {
                return new UserAlreadyExistsException(field);
            }
        }
        return exception;
    }
}
