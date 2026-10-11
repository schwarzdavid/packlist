package app.schwarzdavid.packlist.user.service.domain.auth.data;

public record RegistrationData(
        String username,
        String email,
        String password
) {
}
