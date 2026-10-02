package app.schwarzdavid.packlist.service.user.domain.user.dto;

public record UserCreateDto(
        String username,
        String email,
        String password
) {
}
