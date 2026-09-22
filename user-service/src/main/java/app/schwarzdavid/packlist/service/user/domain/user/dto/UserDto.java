package app.schwarzdavid.packlist.service.user.domain.user.dto;

public record UserDto(
        Long userId,
        String username,
        String email
) {
}
