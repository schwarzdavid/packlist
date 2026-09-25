package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.service.user.domain.auth.data.AuthData;
import app.schwarzdavid.packlist.service.user.domain.auth.data.CredentialsData;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.CredentialsDto;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.RegistrationDto;
import app.schwarzdavid.packlist.service.user.domain.user.UserService;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserCreateDto;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthHandler {
    private final CredentialsService credentialsService;
    private final UserService userService;
    private final JwtService jwtService;

    public void login(RoutingContext ctx) {
        var credentials = ctx.body().asPojo(CredentialsDto.class);

        credentialsService.authenticate(new CredentialsData(credentials.identifier(), credentials.password()))
                .onSuccess(ctx::json)
                .onFailure(ctx::fail);
    }

    public void register(RoutingContext ctx) {
        var credentials = ctx.body().asPojo(RegistrationDto.class);

        userService.createUser(new UserCreateDto(credentials.email(), credentials.username(), credentials.password()))
                .map(user -> new AuthData(user.userId(), jwtService.generateTokenForUser(user), user))
                .onSuccess(ctx::json)
                .onFailure(ctx::fail);
    }
}
