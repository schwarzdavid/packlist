package app.schwarzdavid.packlist.common.web;

import app.schwarzdavid.packlist.common.web.dto.ProblemDetailsDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;


public class CommonControllerAdviceIT {
    private static final RestTestClient client = RestTestClient.bindToController(TestController.class).configureServer(server -> server.setControllerAdvice(CommonControllerAdvice.class)).build();

    @Test
    void shouldReturnProblemDetailsOnUnknownError() {
        var result = client.get().uri("/test/exception").exchange().expectStatus().is5xxServerError().expectBody(ProblemDetailsDto.class).returnResult().getResponseBody();

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getDetail()).isNull();
        assertThat(result.getFields()).isEmpty();
    }

    @RestController
    static class TestController {
        @GetMapping("/test/exception")
        public void testException() {
            throw new RuntimeException("Test exception");
        }
    }
}
