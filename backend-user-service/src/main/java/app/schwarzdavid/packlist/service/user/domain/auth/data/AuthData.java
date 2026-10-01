package app.schwarzdavid.packlist.service.user.domain.auth.data;

import app.schwarzdavid.packlist.common.data.UserData;

public record AuthData(
        Long userId,
        String token,
        UserData user
) {
}
