package app.schwarzdavid.packlist.service.user.domain.auth.data;

import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;

public record AuthData(
        Long userId,
        String token,
        UserData user
) {
}
