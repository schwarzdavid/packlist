package app.schwarzdavid.packlist.user.service.domain.user;

import app.schwarzdavid.packlist.user.service.domain.user.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserProvider {
    public Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken token) {
            var principal = (UserPrincipal) token.getPrincipal();
            if (principal == null) {
                throw new IllegalStateException("No authentication found");
            }
            return principal.getUserId();
        }
        if (authentication instanceof JwtAuthenticationToken token) {
            var subject = (String) token.getPrincipal();
            return Long.parseLong(subject);
        }
        throw new IllegalStateException("No authentication found");
    }
}
