package app.schwarzdavid.packlist.user.service.domain.user;

import app.schwarzdavid.packlist.user.service.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
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
}
