package app.schwarzdavid.packlist.service.user.domain.auth.dto;

import app.schwarzdavid.packlist.service.user.domain.user.dto.UserDto;

public record AuthDto(
        Long userId,
        String token,
        UserDto user
) {
}
