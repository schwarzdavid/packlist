package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.common.JwtService;
import app.schwarzdavid.packlist.service.user.domain.auth.data.AuthData;
import app.schwarzdavid.packlist.service.user.domain.auth.data.CredentialsData;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.AuthDto;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.CredentialsDto;
import app.schwarzdavid.packlist.service.user.domain.auth.dto.RegistrationDto;
import app.schwarzdavid.packlist.service.user.domain.user.UserService;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserDto;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthHandler {
    private final CredentialsService credentialsService;
    private final UserService userService;
    private final JwtService jwtService;

    public Future<AuthDto> login(RoutingContext ctx) {
        var credentials = ctx.body().asPojo(CredentialsDto.class);

        return credentialsService.authenticate(new CredentialsData(credentials.identifier(), credentials.password()))
                .map(this::mapAuthDataToDto);
    }

    public Future<AuthDto> register(RoutingContext ctx) {
        var credentials = ctx.body().asPojo(RegistrationDto.class);

        return userService.createUser(new UserCreateData(credentials.username(), credentials.email(), credentials.password()))
                .map(user -> new AuthData(user.userId(), jwtService.generateTokenForUser(user), user))
                .map(this::mapAuthDataToDto)
                .onSuccess(_ -> ctx.response().setStatusCode(201));
    }

    private AuthDto mapAuthDataToDto(AuthData authData) {
        return new AuthDto(
                authData.userId(),
                authData.token(),
                new UserDto(
                        authData.user().userId(),
                        authData.user().username(),
                        authData.user().email()
                )
        );
    }
}
