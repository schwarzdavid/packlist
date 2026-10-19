package app.schwarzdavid.packlist.user.service.domain.user.mapper;

import app.schwarzdavid.packlist.common.security.UserPrincipal;
import app.schwarzdavid.packlist.user.api.model.UserDto;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import app.schwarzdavid.packlist.user.service.domain.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", source = "hashedPassword")
    UserEntity toEntity(UserCreateData userCreateData);

    UserData toData(UserEntity userEntity);

    UserData toData(UserPrincipal userPrincipal);

    UserDto toDto(UserData userData);
}
