package app.schwarzdavid.packlist.user.service.domain.user.security;

import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    @Getter
    @NonNull
    private final Long userId;

    @Getter
    @NonNull
    private final String username;

    @Getter
    @NonNull
    private final String email;
    private final String passwordHash;

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    public UserData getUserData() {
        return new UserData(userId, username, email);
    }
}
