package app.schwarzdavid.packlist.service.user.fixture;

import app.schwarzdavid.packlist.service.user.domain.auth.data.CredentialsData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserDataWithPassword;

public class UserFixtures {
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_USERNAME = "username";
    public static final String DEFAULT_EMAIL = "email@example.com";
    public static final String DEFAULT_PASSWORD = "plain-password";

    private UserFixtures() {}

    public static UserDataWithPassword withPassword() {
        return new UserDataWithPassword(DEFAULT_ID, DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    public static UserData userData() {
        return new UserData(DEFAULT_ID, DEFAULT_USERNAME, DEFAULT_EMAIL);
    }

    public static CredentialsData credentialsWithUsernameIdentity() {
        return new CredentialsData(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public static CredentialsData credentialsWithEmailIdentity() {
        return new CredentialsData(DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    public static UserCreateData create() {
        return new UserCreateData(DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }
}
