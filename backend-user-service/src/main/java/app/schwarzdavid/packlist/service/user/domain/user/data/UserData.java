package app.schwarzdavid.packlist.service.user.domain.user.data;

public record UserData(
        Long userId,
        String username,
        String email
) {
}
