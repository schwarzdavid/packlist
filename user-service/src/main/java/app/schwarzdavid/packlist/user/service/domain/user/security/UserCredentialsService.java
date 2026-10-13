package app.schwarzdavid.packlist.user.service.domain.user.security;

import app.schwarzdavid.packlist.user.service.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCredentialsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String identifier) throws UsernameNotFoundException {
        var user = userRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        return UserPrincipal.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .passwordHash(user.getPassword())
                .build();
    }
}
