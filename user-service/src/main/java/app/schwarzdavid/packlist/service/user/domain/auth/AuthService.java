package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.common.JwtService;
import app.schwarzdavid.packlist.service.user.domain.auth.data.AuthData;
import app.schwarzdavid.packlist.service.user.domain.user.UserService;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import io.vertx.core.Future;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;

    public Future<AuthData> registerUser(UserCreateData userCreateData) {
        return userService.createUser(userCreateData)
                .map(userData -> new AuthData(
                        userData.userId(),
                        jwtService.generateTokenForUser(userData),
                        userData
                ));
    }
}
