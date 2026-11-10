package app.schwarzdavid.packlist.common.security;

import app.schwarzdavid.packlist.common.annotation.EnablePacklistCommon;
import app.schwarzdavid.packlist.common.web.dto.ProblemDetailsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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

@AutoConfigureMockMvc
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SecurityControllerAdviceIT {
    @Autowired
    private RestTestClient client;

    @Test
    void shouldReturnUnauthorizedOnUnauthorizedError() {
        var result = client.get().uri("/test/unauthorized").exchange().expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED).expectBody(ProblemDetailsDto.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Unauthorized");
        assertThat(result.getStatus()).isEqualTo(401);
        assertThat(result.getDetail()).isNull();
        assertThat(result.getFields()).isEmpty();
    }

    @SpringBootApplication
    static class TestApplication {
    }

    @EnablePacklistCommon
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
        @GetMapping("/test/unauthorized")
        public void testUnauthorized() {
            // do nothing
        }
    }
}
