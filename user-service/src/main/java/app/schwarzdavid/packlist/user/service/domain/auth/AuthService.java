package app.schwarzdavid.packlist.user.service.domain.auth;

import app.schwarzdavid.packlist.user.service.domain.auth.data.AuthData;
import app.schwarzdavid.packlist.user.service.domain.auth.data.CredentialsData;
import app.schwarzdavid.packlist.user.service.domain.auth.data.RegistrationData;
import app.schwarzdavid.packlist.user.service.domain.auth.exception.InvalidCredentialsException;
import app.schwarzdavid.packlist.user.service.domain.user.UserService;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserDataWithPassword;
import app.schwarzdavid.packlist.user.service.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthData registerUser(RegistrationData registrationData) {
        var hashedPassword = passwordEncoder.encode(registrationData.password());
        var userData = userService.createUser(new UserCreateData(
                registrationData.username(),
                registrationData.email(),
                hashedPassword
        ));
        return createAuthDataForUser(userData);
    }

    public AuthData loginUser(CredentialsData credentialsData) {
        UserDataWithPassword userDataWithPassword;
        try {
            userDataWithPassword = userService.getUserByIdentifier(credentialsData.identifier());
        } catch (UserNotFoundException e) {
            throw new InvalidCredentialsException();
        }
        if (isPasswordValid(credentialsData.password(), userDataWithPassword.hashedPassword())) {
            return createAuthDataForUser(userDataWithPassword);
        }

        throw new InvalidCredentialsException();
    }

    private boolean isPasswordValid(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    private AuthData createAuthDataForUser(UserData userData) {
        String token = jwtService.generateToken(userData);

        return new AuthData(userData.userId(), token, userData);
    }

    private AuthData createAuthDataForUser(UserDataWithPassword userData) {
        return createAuthDataForUser(new UserData(userData.userId(), userData.username(), userData.email()));
    }
}
