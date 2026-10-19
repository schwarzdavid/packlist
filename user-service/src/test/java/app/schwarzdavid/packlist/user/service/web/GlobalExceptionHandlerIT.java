package app.schwarzdavid.packlist.user.service.web;

import app.schwarzdavid.packlist.user.api.model.ProblemDetailsDto;
import app.schwarzdavid.packlist.user.service.fixture.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class GlobalExceptionHandlerIT {
    @Autowired
    private RestTestClient client;

    @Test
    void shouldReturnProblemDetailsOnUnknownError() {
        var result = client.get().uri("/test/exception").exchange().expectStatus().is5xxServerError().expectBody(ProblemDetailsDto.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getDetail()).isNull();
        assertThat(result.getFields()).isEmpty();
    }

    @Test
    void shouldReturnUnauthorizedOnUnauthorizedError() {
        var result = client.get().uri("/test/unauthorized").exchange().expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED).expectBody(ProblemDetailsDto.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Unauthorized");
        assertThat(result.getStatus()).isEqualTo(401);
        assertThat(result.getDetail()).isNull();
        assertThat(result.getFields()).isEmpty();
    }

    @Import(TestController.class)
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        SecurityFilterChain testEndpoints(HttpSecurity http) {
            return http.securityMatcher("/test/**")
                    .authorizeHttpRequests(auth -> {
                        auth.requestMatchers(HttpMethod.GET, "/test/exception").permitAll();
                        auth.anyRequest().authenticated();
                    })
                    .build();
        }
    }

    @RestController
    static class TestController {
        @GetMapping("/test/exception")
        public void testException() {
            throw new RuntimeException("Test exception");
        }

        @GetMapping("/test/unauthorized")
        public void testUnauthorized() {
            // do nothing
        }
    }
}
