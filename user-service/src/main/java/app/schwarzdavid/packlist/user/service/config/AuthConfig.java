package app.schwarzdavid.packlist.user.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthConfig {
    @Value("${user-service.auth.password.strength}")
    private Integer passwordStrength;

    @Bean
    public PasswordEncoder createPasswordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B, passwordStrength);
    }
}
