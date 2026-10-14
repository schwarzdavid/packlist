package app.schwarzdavid.packlist.user.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtConfig {
    @Value("${user-service.auth.jwt.secret}")
    private String jwtSecret;

    @Bean
    public JwtDecoder createJwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey()).build();
    }

    @Bean
    public JwtEncoder createJwtEncoder() {
        return NimbusJwtEncoder.withSecretKey(getSecretKey()).build();
    }

    private SecretKey getSecretKey() {
        return new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
    }
}
