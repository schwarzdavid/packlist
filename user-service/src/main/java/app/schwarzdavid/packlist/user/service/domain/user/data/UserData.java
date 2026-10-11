package app.schwarzdavid.packlist.user.service.domain.user.data;

public record UserData(
        Long userId,
        String username,
        String email
) {
}
