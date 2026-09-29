package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserDataWithPassword;
import app.schwarzdavid.packlist.service.user.service.security.PasswordHashingService;
import io.vertx.core.Future;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;

    public Future<UserData> createUser(UserCreateData userCreateDto) {
        return passwordHashingService.hashPassword(userCreateDto.password())
                .compose(hashedPassword -> userRepository.createUser(userCreateDto, hashedPassword));
    }

    public Future<Optional<UserDataWithPassword>> getUserDataWithPasswordByIdentifier(String identifier) {
        return userRepository.findUserWithPasswordByIdentifier(identifier);
    }
}
