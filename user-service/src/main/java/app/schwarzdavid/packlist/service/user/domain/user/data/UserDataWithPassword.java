package app.schwarzdavid.packlist.service.user.domain.user.data;

public record UserDataWithPassword(
        Long userId,
        String username,
        String email,
        String password
) {
}
