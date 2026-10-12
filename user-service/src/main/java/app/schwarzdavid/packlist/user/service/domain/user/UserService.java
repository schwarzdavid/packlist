package app.schwarzdavid.packlist.user.service.domain.user;

import app.schwarzdavid.packlist.user.service.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserDataWithPassword;
import app.schwarzdavid.packlist.user.service.domain.user.exception.UserNotFoundException;
import app.schwarzdavid.packlist.user.service.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserData createUser(UserCreateData userCreateData) {
        var entity = userMapper.toNewUserEntity(userCreateData);
        userRepository.save(entity);
        return userMapper.toUserData(entity);
    }

    public UserDataWithPassword getUserByIdentifier(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier, identifier).map(userMapper::toUserDataWithPassword)
                .orElseThrow(UserNotFoundException::new);
    }
}
