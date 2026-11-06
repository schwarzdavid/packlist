package app.schwarzdavid.packlist.common.test.security;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@NullMarked
public class WithJwtUserSecurityContextFactory implements WithSecurityContextFactory<WithJwtUser> {
    @Override
    public SecurityContext createSecurityContext(WithJwtUser annotation) {
        var context = SecurityContextHolder.createEmptyContext();
        var authentication = new JwtAuthenticationToken(Jwt.withTokenValue("token").header("alg", "none").subject(String.valueOf(annotation.id())).build());

        context.setAuthentication(authentication);

        return context;
    }
}
