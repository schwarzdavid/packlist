package app.schwarzdavid.packlist.user.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain createSecurityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(matcher -> {
                    // TODO: replace with @PublicEndpoint annotation
                    matcher.requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll();
                    matcher.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
                    matcher.requestMatchers(HttpMethod.GET, "/actuator/**").permitAll();
                    matcher.anyRequest().authenticated();
                })
                .build();
    }
}
