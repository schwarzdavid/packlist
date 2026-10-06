package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.common.data.UserData;
import app.schwarzdavid.packlist.common.exception.db.UniqueConstraintValidationException;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserDataWithPassword;
import app.schwarzdavid.packlist.service.user.domain.user.exception.UserAlreadyExistsException;
import app.schwarzdavid.packlist.service.user.infrastructure.security.PasswordHashingService;
import io.vertx.core.Future;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserService {
    private static final String USERNAME_CONSTRAINT_NAME = "uq_users_username";
    private static final String EMAIL_CONSTRAINT_NAME = "uq_users_email";

    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;

    public Future<UserData> createUser(UserCreateData userCreateDto) {
        return passwordHashingService.hashPassword(userCreateDto.password())
                .compose(hashedPassword -> userRepository.createUser(userCreateDto, hashedPassword))
                .recover(throwable -> {
                    if (throwable instanceof UniqueConstraintValidationException e) {
                        UserAlreadyExistsException.Field field = getUserField(e);
                        if (field != null) {
                            return Future.failedFuture(new UserAlreadyExistsException(field, e));
                        }
                    }
                    return Future.failedFuture(throwable);
                });
    }

    public Future<Optional<UserDataWithPassword>> getUserDataWithPasswordByIdentifier(String identifier) {
        return userRepository.findUserWithPasswordByIdentifier(identifier);
    }

    private UserAlreadyExistsException.Field getUserField(UniqueConstraintValidationException exception) {
        return switch (exception.getConstraint()) {
            case USERNAME_CONSTRAINT_NAME -> UserAlreadyExistsException.Field.USERNAME;
            case EMAIL_CONSTRAINT_NAME -> UserAlreadyExistsException.Field.EMAIL;
            default -> null;
        };
    }
}
