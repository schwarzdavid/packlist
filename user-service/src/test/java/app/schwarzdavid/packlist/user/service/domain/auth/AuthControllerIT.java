package app.schwarzdavid.packlist.user.service.domain.auth;

import app.schwarzdavid.packlist.user.api.model.*;
import app.schwarzdavid.packlist.user.service.domain.user.UserRepository;
import app.schwarzdavid.packlist.user.service.domain.user.entity.UserEntity;
import app.schwarzdavid.packlist.user.service.fixture.ApiTest;
import app.schwarzdavid.packlist.user.service.fixture.AuthFixtures;
import app.schwarzdavid.packlist.user.service.fixture.UserFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerIT extends ApiTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Nested
    public class Register {
        private static final String URI = "/api/auth/register";

        public static Stream<Arguments> invalidRegistrationDtos() {
            return Stream.of(
                    Arguments.of(new RegistrationDto("username", "noemail", "12345678")),
                    Arguments.of(new RegistrationDto("email@example.com", "email@example.com", "12345678")),
                    Arguments.of(new RegistrationDto("username", "email@example.com", "short")),
                    Arguments.of(new RegistrationDto(null, "email@example.com", "12345678")),
                    Arguments.of(new RegistrationDto("username", null, "12345678")),
                    Arguments.of(new RegistrationDto("username", "email@example.com", null)),
                    Arguments.of(new RegistrationDto("", "email@example.com", "12345678")),
                    Arguments.of(new RegistrationDto("username", "", "12345678")),
                    Arguments.of(new RegistrationDto("username", "email@example.com", ""))
            );
        }

        public static Stream<Arguments> validRegistrationDtos() {
            return Stream.of(
                    Arguments.of(new RegistrationDto("username", "email@example.com", "12345678")),
                    Arguments.of(new RegistrationDto("USERNAME", "email@example.com", "12345678")),
                    Arguments.of(new RegistrationDto("username", "EMAIL@EXAMPLE.COM", "12345678")),
                    Arguments.of(new RegistrationDto("§$%&/(", "email+foo@example.com", "12345678"))
            );
        }

        @ParameterizedTest
        @MethodSource("validRegistrationDtos")
        void shouldRegisterNewUser(RegistrationDto registrationDto) {
            var response = client.post()
                    .uri(URI)
                    .body(registrationDto)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(AuthDto.class)
                    .value(auth -> {
                        assertThat(auth).isNotNull();
                        assertThat(auth.getUserId()).isPositive();
                        assertThat(auth.getUser().getUserId()).isPositive();

                        var expectedAuth = new AuthDto(1L, "token", new UserDto(1L, registrationDto.getUsername(), registrationDto.getEmail()));
                        assertThat(auth).usingRecursiveComparison().ignoringFields("token", "userId", "user.id").isEqualTo(expectedAuth);
                    });

            var existingUsers = userRepository.findAll();

            assertThat(existingUsers).hasSize(1);
            assertThat(existingUsers.getFirst().getUserId()).isSameAs(Objects.requireNonNull(response.returnResult().getResponseBody()).getUserId());
        }

        @Test
        void shouldNotRegisterWhenUsernameIsTaken() {
            var user = UserEntity.builder()
                    .username(UserFixtures.USERNAME)
                    .email("different@email.com")
                    .password(UserFixtures.HASHED_PASSWORD)
                    .build();
            userRepository.save(user);

            var result = client.post()
                    .uri(URI)
                    .body(AuthFixtures.registrationDto())
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ProblemDetails.class);

            var response = result.returnResult().getResponseBody();
            var existingUsers = userRepository.findAll();

            var expectedProblem = ProblemDetails.builder()
                    .title("Conflict")
                    .detail("User already exists")
                    .status(HttpStatus.CONFLICT.value())
                    .fields(Map.of("username", ProblemDetailsField.builder().path("username").errors(List.of("validation.usernameAlreadyExists")).build()))
                    .build();

            assertThat(response).isNotNull();
            assertThat(response).isEqualTo(expectedProblem);
            assertThat(existingUsers).hasSize(1);
        }

        @Test
        void shouldNotRegisterWhenEmailIsTaken() {
            var user = UserEntity.builder()
                    .username("otherusername")
                    .email(UserFixtures.EMAIL)
                    .password(UserFixtures.HASHED_PASSWORD)
                    .build();
            userRepository.save(user);

            var result = client.post()
                    .uri(URI)
                    .body(AuthFixtures.registrationDto())
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ProblemDetails.class);

            var response = result.returnResult().getResponseBody();
            var existingUsers = userRepository.findAll();

            var expectedProblem = ProblemDetails.builder()
                    .title("Conflict")
                    .detail("User already exists")
                    .status(HttpStatus.CONFLICT.value())
                    .fields(Map.of("email", ProblemDetailsField.builder().path("email").errors(List.of("validation.emailAlreadyExists")).build()))
                    .build();

            assertThat(response).isNotNull();
            assertThat(response).isEqualTo(expectedProblem);
            assertThat(existingUsers).hasSize(1);
        }

        @ParameterizedTest
        @MethodSource("invalidRegistrationDtos")
        void shouldNotRegisterWhenInvalidRegistrationDtoIsGiven(RegistrationDto registrationDto) {
            var result = client.post()
                    .uri(URI)
                    .body(registrationDto)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                    .expectBody(Object.class);
        }
    }

    @Nested
    public class Login {
        private static final String URI = "/api/auth/login";

        @Test
        void shouldLoginWithValidUsernameCredentials() {
            var existingUser = UserEntity.builder()
                    .username(UserFixtures.USERNAME)
                    .email(UserFixtures.EMAIL)
                    .password(passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD))
                    .build();
            userRepository.save(existingUser);

            var result = client.post().uri(URI)
                    .body(new CredentialsDto(UserFixtures.USERNAME, AuthFixtures.PLAIN_PASSWORD))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(AuthDto.class)
                    .returnResult()
                    .getResponseBody();

            var expectedAuth = new AuthDto(existingUser.getUserId(), "token", new UserDto(existingUser.getUserId(), UserFixtures.USERNAME, UserFixtures.EMAIL));

            assertThat(result).isNotNull();
            assertThat(result).usingRecursiveComparison().ignoringFields("token").isEqualTo(expectedAuth);
            assertThat(result.getToken()).isNotEmpty();
        }

        @Test
        void shouldLoginWithValidEmailCredentials() {
            var existingUser = UserEntity.builder()
                    .username(UserFixtures.USERNAME)
                    .email(UserFixtures.EMAIL)
                    .password(passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD))
                    .build();
            userRepository.save(existingUser);

            var result = client.post().uri(URI)
                    .body(new CredentialsDto(UserFixtures.EMAIL, AuthFixtures.PLAIN_PASSWORD))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(AuthDto.class)
                    .returnResult()
                    .getResponseBody();

            var expectedAuth = new AuthDto(existingUser.getUserId(), "token", new UserDto(existingUser.getUserId(), UserFixtures.USERNAME, UserFixtures.EMAIL));

            assertThat(result).isNotNull();
            assertThat(result).usingRecursiveComparison().ignoringFields("token").isEqualTo(expectedAuth);
            assertThat(result.getToken()).isNotEmpty();
        }

        @Test
        void shouldRejectLoginWithWrongIdentifier() {
            var existingUser = UserEntity.builder()
                    .username(UserFixtures.USERNAME)
                    .email(UserFixtures.EMAIL)
                    .password(passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD))
                    .build();
            userRepository.save(existingUser);

            var result = client.post().uri(URI)
                    .body(new CredentialsDto("unknown", AuthFixtures.PLAIN_PASSWORD))
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody(ProblemDetails.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(result).isNotNull();
            assertThat(result.getDetail()).isEqualTo("Invalid credentials");
        }

        @Test
        void shouldRejectLoginWithWrongPassword() {
            var existingUser = UserEntity.builder()
                    .username(UserFixtures.USERNAME)
                    .email(UserFixtures.EMAIL)
                    .password(passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD))
                    .build();
            userRepository.save(existingUser);

            var result = client.post().uri(URI)
                    .body(new CredentialsDto(UserFixtures.USERNAME, "wrong-password"))
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody(ProblemDetails.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(result).isNotNull();
            assertThat(result.getDetail()).isEqualTo("Invalid credentials");
        }

        @Test
        void shouldRejectLoginWithSameReasonWhenIdentifierOrPasswordIsWrong() {
            var existingUser = UserEntity.builder()
                    .username(UserFixtures.USERNAME)
                    .email(UserFixtures.EMAIL)
                    .password(passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD))
                    .build();
            userRepository.save(existingUser);

            var wrongIdentifierResult = client.post().uri(URI)
                    .body(new CredentialsDto("unknown", AuthFixtures.PLAIN_PASSWORD))
                    .exchange()
                    .expectBody(ProblemDetails.class)
                    .returnResult()
                    .getResponseBody();

            var wrongPasswordResult = client.post().uri(URI)
                    .body(new CredentialsDto(UserFixtures.USERNAME, "wrong-password"))
                    .exchange()
                    .expectBody(ProblemDetails.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(wrongIdentifierResult).isNotNull();
            assertThat(wrongPasswordResult).isNotNull();
            assertThat(wrongIdentifierResult).isEqualTo(wrongPasswordResult);
        }
    }
}
