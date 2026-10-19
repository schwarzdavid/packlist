package app.schwarzdavid.packlist.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class JwtDecoderConfig {
    @Bean
    @ConditionalOnMissingBean
    public JwtDecoder createJwtDecoder(Environment environment) {
        var issuerHost = environment.getProperty("USER_SERVICE_HOSTNAME", "http://localhost:8881");
        if (!issuerHost.startsWith("http")) {
            throw new IllegalArgumentException("USER_SERVICE_HOSTNAME must be a valid URL");
        }
        var issuerUri = UriComponentsBuilder.fromUriString(issuerHost).path("/.well-known/jwks.json").toString();

        return NimbusJwtDecoder.withJwkSetUri(issuerUri).build();
    }
}
