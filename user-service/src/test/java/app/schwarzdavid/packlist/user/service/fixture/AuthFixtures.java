package app.schwarzdavid.packlist.user.service.fixture;

import app.schwarzdavid.packlist.user.service.domain.auth.data.CredentialsData;
import app.schwarzdavid.packlist.user.service.domain.auth.data.RegistrationData;

public class AuthFixtures {
    public static final String PLAIN_PASSWORD = "plain-password";
    public static final String HASHED_PASSWORD = "hashed-password";
    public static final String JWT = "jwt-token";

    public static RegistrationData registrationData() {
        return new RegistrationData(UserFixtures.USERNAME, UserFixtures.EMAIL, AuthFixtures.PLAIN_PASSWORD);
    }

    public static CredentialsData credentialsData() {
        return new CredentialsData(UserFixtures.USERNAME, AuthFixtures.PLAIN_PASSWORD);
    }
}
