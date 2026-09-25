package app.schwarzdavid.packlist.service.user.domain.auth.data;

public record CredentialsData(
        String identifier,
        String password
) {
}
