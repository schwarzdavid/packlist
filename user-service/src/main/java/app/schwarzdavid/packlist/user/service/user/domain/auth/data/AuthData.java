package app.schwarzdavid.packlist.user.service.user.domain.auth.data;

import app.schwarzdavid.packlist.user.service.user.domain.user.UserData;

public record AuthData(
        Integer userId,
        String token,
        UserData user
) {
}
