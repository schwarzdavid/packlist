package app.schwarzdavid.packlist.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtDecoderConfig {
    @Value("${user-service.auth.jwt.secret}")
    private String jwtSecret;

    @Bean
    public JwtDecoder createJwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey()).build();
    }

    private SecretKey getSecretKey() {
        return new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
    }
}
