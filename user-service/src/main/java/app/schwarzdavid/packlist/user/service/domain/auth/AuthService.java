package app.schwarzdavid.packlist.user.service.domain.auth;

import app.schwarzdavid.packlist.common.security.UserPrincipal;
import app.schwarzdavid.packlist.user.service.domain.auth.data.AuthData;
import app.schwarzdavid.packlist.user.service.domain.auth.data.CredentialsData;
import app.schwarzdavid.packlist.user.service.domain.auth.data.RegistrationData;
import app.schwarzdavid.packlist.user.service.domain.user.UserService;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import app.schwarzdavid.packlist.user.service.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

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
        var authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(credentialsData.identifier(), credentialsData.password());
        var authentication = authenticationManager.authenticate(authenticationToken);
        var principal = (UserPrincipal) authentication.getPrincipal();

        if (principal == null) {
            throw new BadCredentialsException("Invalid credentials");
        }

        var userData = userMapper.toData(principal);

        return createAuthDataForUser(userData);
    }

    private AuthData createAuthDataForUser(UserData userData) {
        return new AuthData(
                userData.userId(),
                jwtService.createJwtForUser(userData),
                userData
        );
    }
}
