package app.schwarzdavid.packlist.user.service.domain.user.data;

public record UserCreateData(
        String username,
        String email,
        String hashedPassword
) {
}
