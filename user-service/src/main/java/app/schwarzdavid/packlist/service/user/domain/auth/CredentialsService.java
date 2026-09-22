package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.service.user.domain.auth.data.AuthData;
import app.schwarzdavid.packlist.service.user.domain.auth.data.CredentialsData;
import app.schwarzdavid.packlist.service.user.domain.user.UserService;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import app.schwarzdavid.packlist.service.user.service.security.PasswordHashingService;
import io.vertx.core.Future;

public class CredentialsService {
    private final AuthRepository authRepository;
    private final UserService userService;
    private final PasswordHashingService passwordHashingService;

    public CredentialsService(AuthRepository authRepository, UserService userService, PasswordHashingService passwordHashingService) {
        this.authRepository = authRepository;
        this.userService = userService;
        this.passwordHashingService = passwordHashingService;
    }

    public Future<AuthData> authenticate(CredentialsData credentials) {
        return userService.getUserDataWithPasswordByIdentifier(credentials.identifier())
                .compose(user -> passwordHashingService
                        .verifyPassword(user.password(), credentials.password()).expecting(result -> result.verified)
                        .map(_ -> new AuthData(
                                user.userId(),
                                "token to be defined",
                                new UserData(
                                        user.userId(),
                                        user.username(),
                                        user.email()
                                )
                        )));
    }
}
