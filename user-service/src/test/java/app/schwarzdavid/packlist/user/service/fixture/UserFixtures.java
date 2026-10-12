package app.schwarzdavid.packlist.user.service.fixture;

import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;

public class UserFixtures {
    public static final Long USER_ID = 1L;
    public static final String USERNAME = "username";
    public static final String EMAIL = "email@example.com";

    public static UserData userData() {
        return new UserData(USER_ID, USERNAME, EMAIL);
    }
}
