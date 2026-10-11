package app.schwarzdavid.packlist.user.service.domain.user.data;

public record UserDataWithPassword(
        Long userId,
        String username,
        String email,
        String hashedPassword
) {
}
