package app.schwarzdavid.packlist.service.user.domain.auth.dto;

public record RegistrationDto(
        String username,
        String email,
        String password
) {
}
