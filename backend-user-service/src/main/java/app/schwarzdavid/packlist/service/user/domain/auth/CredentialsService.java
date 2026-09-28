package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.service.user.domain.auth.data.AuthData;
import app.schwarzdavid.packlist.service.user.domain.auth.data.CredentialsData;
import app.schwarzdavid.packlist.service.user.domain.auth.exception.InvalidCredentialsException;
import app.schwarzdavid.packlist.service.user.domain.user.UserService;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import app.schwarzdavid.packlist.service.user.service.security.PasswordHashingService;
import io.vertx.core.Future;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CredentialsService {
    private final UserService userService;
    private final PasswordHashingService passwordHashingService;
    private final JwtService jwtService;

    public Future<AuthData> authenticate(CredentialsData credentials) {
        return userService.getUserDataWithPasswordByIdentifier(credentials.identifier())
                .compose(userOptional -> {
                    var user = userOptional.orElseThrow(InvalidCredentialsException::new);

                    return passwordHashingService
                            .verifyPassword(user.password(), credentials.password())
                            .map(isPasswordValid -> {
                                if (!isPasswordValid) {
                                    throw new InvalidCredentialsException();
                                }
                                var userData = new UserData(user.userId(), user.username(), user.email());
                                return new AuthData(user.userId(), jwtService.generateTokenForUser(userData), userData);
                            });
                });
    }
}
