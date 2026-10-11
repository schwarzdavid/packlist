package app.schwarzdavid.packlist.user.service.user.domain.auth;

import app.schwarzdavid.packlist.user.api.AuthApi;
import app.schwarzdavid.packlist.user.api.model.AuthDto;
import app.schwarzdavid.packlist.user.api.model.CredentialsDto;
import app.schwarzdavid.packlist.user.api.model.RegistrationDto;
import app.schwarzdavid.packlist.user.service.user.domain.auth.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthService authService;
    private final AuthMapper authMapper;

    @Override
    public ResponseEntity<AuthDto> login(CredentialsDto credentialsDto) {
        return null;
    }

    @Override
    public ResponseEntity<AuthDto> register(RegistrationDto registrationDto) {
        var registrationData = authMapper.toRegistrationData(registrationDto);
        var authData = authService.registerUser(registrationData);
        var authDto = authMapper.toRegistrationDto(authData);

        return ResponseEntity.ok(authDto);
    }
}
