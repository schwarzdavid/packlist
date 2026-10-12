package app.schwarzdavid.packlist.user.service.domain.user.mapper;

import app.schwarzdavid.packlist.user.service.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserDataWithPassword;
import app.schwarzdavid.packlist.user.service.domain.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", source = "hashedPassword")
    UserEntity toNewUserEntity(UserCreateData userCreateData);

    UserData toUserData(UserEntity userEntity);

    @Mapping(target = "hashedPassword", source = "password")
    UserDataWithPassword toUserDataWithPassword(UserEntity userEntity);
}
