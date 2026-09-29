package app.schwarzdavid.packlist.service.user.domain.user.data;

public record UserCreateData(
        String username,
        String email,
        String password
) {
}
