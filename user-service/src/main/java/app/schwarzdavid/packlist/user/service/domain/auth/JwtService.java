package app.schwarzdavid.packlist.user.service.domain.auth;

import app.schwarzdavid.packlist.user.service.config.properties.JwtProperties;
import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public String createJwtForUser(UserData userData) {
        var issuedAt = Instant.now();
        var claims = JwtClaimsSet.builder()
                .subject(userData.userId().toString())
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plus(jwtProperties.ttl()))
                .issuer(jwtProperties.issuer())
                .build();
        var params = JwtEncoderParameters.from(claims);

        return jwtEncoder.encode(params).getTokenValue();
    }
}
