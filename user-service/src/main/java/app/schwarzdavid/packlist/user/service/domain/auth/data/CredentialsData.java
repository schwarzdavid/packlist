package app.schwarzdavid.packlist.user.service.domain.auth.data;

public record CredentialsData(
        String identifier,
        String password
) {
}
