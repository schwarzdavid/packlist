package app.schwarzdavid.packlist.user.service.user.domain.auth.mapper;

import app.schwarzdavid.packlist.user.api.model.AuthDto;
import app.schwarzdavid.packlist.user.api.model.RegistrationDto;
import app.schwarzdavid.packlist.user.service.user.domain.auth.data.AuthData;
import app.schwarzdavid.packlist.user.service.user.domain.auth.data.RegistrationData;
import org.mapstruct.Mapper;

@Mapper
public interface AuthMapper {
    RegistrationData toRegistrationData(RegistrationDto registrationDto);

    AuthDto toRegistrationDto(AuthData authData);
}
