package app.schwarzdavid.packlist.user.service.config.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties("user-service.auth.jwt")
public record JwtProperties(
        @NotNull Resource privateKey,
        @NotNull Resource publicKey,
        Duration ttl,
        @NotNull String issuer
) {
}
