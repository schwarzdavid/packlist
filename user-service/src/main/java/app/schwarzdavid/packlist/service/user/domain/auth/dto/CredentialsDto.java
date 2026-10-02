package app.schwarzdavid.packlist.service.user.domain.auth.dto;

public record CredentialsDto(
        String identifier,
        String password
) {
}
