package app.schwarzdavid.packlist.common.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserProvider {
    public Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated");
        }
        var principal = authentication.getPrincipal();
        return switch (principal) {
            case UserPrincipal user -> getUserIdFromPrincipal(user);
            case Jwt jwt -> getUserIdFromPrincipal(jwt);
            case null, default -> throw new IllegalStateException("Unknown principal type");
        };
    }

    private Long getUserIdFromPrincipal(UserPrincipal principal) {
        return principal.getUserId();
    }

    private Long getUserIdFromPrincipal(Jwt jwt) {
        var subject = jwt.getSubject();
        if (subject == null) {
            throw new IllegalStateException("JWT has no subject");
        }
        return Long.parseLong(subject);
    }
}
