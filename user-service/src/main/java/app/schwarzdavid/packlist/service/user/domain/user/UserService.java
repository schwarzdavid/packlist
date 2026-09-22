package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.service.user.domain.user.data.UserDataWithPassword;
import app.schwarzdavid.packlist.service.user.service.security.PasswordHashingService;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserCreateDto;
import io.vertx.core.Future;

public class UserService {
    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;

    public UserService(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    public Future<UserData> createUser(UserCreateDto userCreateDto) {
        return passwordHashingService.hashPassword(userCreateDto.password())
                .compose(hashedPassword -> userRepository.createUser(userCreateDto, hashedPassword));
    }

    public Future<UserDataWithPassword> getUserDataWithPasswordByIdentifier(String identifier) {
        return userRepository.findUserWithPasswordByIdentifier(identifier);
    }
}
