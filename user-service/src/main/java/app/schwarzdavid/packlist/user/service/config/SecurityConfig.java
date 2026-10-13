package app.schwarzdavid.packlist.user.service.config;

import app.schwarzdavid.packlist.user.service.web.security.SecurityExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain createSecurityFilterChain(HttpSecurity http, SecurityExceptionHandler exceptionHandler) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(matcher -> {
                    // TODO: replace with @PublicEndpoint annotation
                    matcher.requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll();
                    matcher.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll();
                    matcher.anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(exceptionHandler)
                        .accessDeniedHandler(exceptionHandler))
                .build();
    }
}
