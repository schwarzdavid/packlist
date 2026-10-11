package app.schwarzdavid.packlist.user.service.user.domain.auth.data;

public record RegistrationData(
        String username,
        String email,
        String password
) {
}
