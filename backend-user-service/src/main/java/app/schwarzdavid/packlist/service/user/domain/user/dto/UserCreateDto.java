package app.schwarzdavid.packlist.service.user.domain.user.dto;

public record UserCreateDto(
        String email,
        String password,
        String username
) {
}
