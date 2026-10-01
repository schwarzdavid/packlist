package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserCreateDto;
import app.schwarzdavid.packlist.service.user.domain.user.dto.UserDto;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Future<UserDto> createUser(RoutingContext ctx) {
        var userCreateDto = ctx.body().asPojo(UserCreateDto.class);

        return userService.createUser(new UserCreateData(userCreateDto.username(), userCreateDto.email(), userCreateDto.password()))
                .map(userData -> new UserDto(userData.userId(), userData.username(), userData.email()))
                .onSuccess(userDto -> ctx.response().setStatusCode(201));
    }
}
