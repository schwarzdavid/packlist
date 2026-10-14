package app.schwarzdavid.packlist.user.service.domain.user;

import app.schwarzdavid.packlist.user.api.model.UserDto;
import app.schwarzdavid.packlist.user.service.fixture.ApiTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserControllerIT extends ApiTest {
    @Nested
    class GetCurrentUser {
        private static final String URI = "/api/users/me";

        @Test
        void shouldGetTheCurrentlyLoggedInUser() {
            var auth = createAuth();

            var result = client.get().uri(URI).header("Authorization", "Bearer %s".formatted(auth.token())).exchange().expectStatus().isOk().expectBody(UserDto.class).returnResult().getResponseBody();

            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(auth.userId());
            assertThat(result.getUsername()).isEqualTo(auth.user().username());
            assertThat(result.getEmail()).isEqualTo(auth.user().email());
        }
    }
}
