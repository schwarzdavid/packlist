package app.schwarzdavid.packlist.user.service.domain.auth;

import app.schwarzdavid.packlist.user.service.domain.auth.data.AuthData;
import app.schwarzdavid.packlist.user.service.domain.auth.data.RegistrationData;
import app.schwarzdavid.packlist.user.service.domain.user.UserService;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserCreateData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthData registerUser(RegistrationData registrationData) {
        var hashedPassword = passwordEncoder.encode(registrationData.password());
        var userData = userService.createUser(new UserCreateData(
                registrationData.username(),
                registrationData.email(),
                hashedPassword
        ));
        return null;
    }
}
