package app.schwarzdavid.packlist.user.service.domain.auth.data;

import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;

public record AuthData(
        Integer userId,
        String token,
        UserData user
) {
}
