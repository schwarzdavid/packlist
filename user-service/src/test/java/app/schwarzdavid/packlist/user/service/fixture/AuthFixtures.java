package app.schwarzdavid.packlist.user.service.fixture;

import app.schwarzdavid.packlist.user.service.domain.auth.data.CredentialsData;
import app.schwarzdavid.packlist.user.service.domain.auth.data.RegistrationData;
import app.schwarzdavid.packlist.user.service.domain.user.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

public class AuthFixtures {
    public static final String IDENTIFIER = UserFixtures.USERNAME;
    public static final String PLAIN_PASSWORD = "plain-password";
    public static final String JWT = "jwt-token";

    public static RegistrationData registrationData() {
        return new RegistrationData(UserFixtures.USERNAME, UserFixtures.EMAIL, PLAIN_PASSWORD);
    }

    public static CredentialsData credentialsData() {
        return new CredentialsData(IDENTIFIER, PLAIN_PASSWORD);
    }

    public static UserPrincipal userPrincipal() {
        return new UserPrincipal(UserFixtures.USER_ID, UserFixtures.USERNAME, UserFixtures.EMAIL, null);
    }

    public static Authentication authentication() {
        return UsernamePasswordAuthenticationToken.authenticated(userPrincipal(), null, List.of());
    }
}
