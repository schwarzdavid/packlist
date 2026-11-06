package app.schwarzdavid.packlist.user.service.domain.user;

import app.schwarzdavid.packlist.common.security.CurrentUserProvider;
import app.schwarzdavid.packlist.user.api.UserApi;
import app.schwarzdavid.packlist.user.api.model.UserDto;
import app.schwarzdavid.packlist.user.service.domain.user.exception.UserNotFoundException;
import app.schwarzdavid.packlist.user.service.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final CurrentUserProvider currentUserProvider;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<UserDto> getCurrentUser() {
        var currentUserId = currentUserProvider.getUserId();
        try {
            var userData = userService.getUserById(currentUserId);
            var userDto = userMapper.toDto(userData);
            return ResponseEntity.ok(userDto);
        } catch (UserNotFoundException ex) {
            throw new UsernameNotFoundException("User not found for principal %d".formatted(currentUserId), ex);
        }
    }
}
