package app.schwarzdavid.packlist.user.service.domain.auth.mapper;

import app.schwarzdavid.packlist.user.api.model.AuthDto;
import app.schwarzdavid.packlist.user.api.model.CredentialsDto;
import app.schwarzdavid.packlist.user.api.model.RegistrationDto;
import app.schwarzdavid.packlist.user.service.domain.auth.data.AuthData;
import app.schwarzdavid.packlist.user.service.domain.auth.data.CredentialsData;
import app.schwarzdavid.packlist.user.service.domain.auth.data.RegistrationData;
import org.mapstruct.Mapper;

@Mapper
public interface AuthMapper {
    RegistrationData toData(RegistrationDto registrationDto);

    CredentialsData toData(CredentialsDto credentialsDto);

    AuthDto toDto(AuthData authData);
}
