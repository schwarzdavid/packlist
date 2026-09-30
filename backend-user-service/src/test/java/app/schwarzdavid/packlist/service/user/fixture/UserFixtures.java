package app.schwarzdavid.packlist.service.user.fixture;

import app.schwarzdavid.packlist.service.user.domain.auth.data.CredentialsData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserDataWithPassword;

public class UserFixtures {
    public static final Long ID = 1L;
    public static final String USERNAME = "username";
    public static final String EMAIL = "email@example.com";
    public static final String PASSWORD = "plain-password";

    private UserFixtures() {}

    public static UserDataWithPassword withPassword() {
        return new UserDataWithPassword(ID, USERNAME, EMAIL, PASSWORD);
    }

    public static UserData userData() {
        return new UserData(ID, USERNAME, EMAIL);
    }

    public static CredentialsData credentialsWithUsernameIdentity() {
        return new CredentialsData(USERNAME, PASSWORD);
    }

    public static CredentialsData credentialsWithEmailIdentity() {
        return new CredentialsData(EMAIL, PASSWORD);
    }

    public static UserCreateData create() {
        return new UserCreateData(USERNAME, EMAIL, PASSWORD);
    }
}
