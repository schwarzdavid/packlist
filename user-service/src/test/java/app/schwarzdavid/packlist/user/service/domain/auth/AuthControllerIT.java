package app.schwarzdavid.packlist.user.service.domain.auth;

import app.schwarzdavid.packlist.common.web.dto.ProblemDetailsDto;
import app.schwarzdavid.packlist.common.web.dto.ProblemDetailsFieldDto;
import app.schwarzdavid.packlist.user.api.model.AuthDto;
import app.schwarzdavid.packlist.user.api.model.CredentialsDto;
import app.schwarzdavid.packlist.user.api.model.RegistrationDto;
import app.schwarzdavid.packlist.user.api.model.UserDto;
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
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerIT extends ApiTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Nested
    public class Register {
        private static final String URI = "/api/auth/register";

        public static Stream<Arguments> invalidRegistrationDtos() {
            return Stream.of(
                    Arguments.of(new RegistrationDto("username", "noemail", "12345678"), "email"),
                    Arguments.of(new RegistrationDto("email@example.com", "email@example.com", "12345678"), "username"),
                    Arguments.of(new RegistrationDto("username", "email@example.com", "short"), "password"),
                    Arguments.of(new RegistrationDto(null, "email@example.com", "12345678"), "username"),
                    Arguments.of(new RegistrationDto("username", null, "12345678"), "email"),
                    Arguments.of(new RegistrationDto("username", "email@example.com", null), "password"),
                    Arguments.of(new RegistrationDto("", "email@example.com", "12345678"), "username"),
                    Arguments.of(new RegistrationDto("username", "", "12345678"), "email"),
                    Arguments.of(new RegistrationDto("username", "email@example.com", ""), "password")
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

        public static Stream<Arguments> existingUsernameRegistrationDtos() {
            return Stream.of(
                    Arguments.of(new RegistrationDto(UserFixtures.USERNAME, "other@email.com", "12345678")),
                    Arguments.of(new RegistrationDto(UserFixtures.USERNAME.toUpperCase(), "other@email.com", "12345678"))
            );
        }

        public static Stream<Arguments> existingEmailRegistrationDtos() {
            return Stream.of(
                    Arguments.of(new RegistrationDto("otherusername", UserFixtures.EMAIL, "12345678")),
                    Arguments.of(new RegistrationDto("otherusername", UserFixtures.EMAIL.toUpperCase(), "12345678"))
            );
        }

        @ParameterizedTest
        @MethodSource("validRegistrationDtos")
        void shouldRegisterNewUser(RegistrationDto registrationDto) {
            var auth = client.post()
                    .uri(URI)
                    .body(registrationDto)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(AuthDto.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(auth).isNotNull();
            assertThat(auth.getUserId()).isPositive();
            assertThat(auth.getUser().getUserId()).isPositive();

            var expectedAuth = new AuthDto(1L, "token", new UserDto(1L, registrationDto.getUsername(), registrationDto.getEmail()));
            assertThat(auth).usingRecursiveComparison().ignoringFields("token", "userId", "user.id").isEqualTo(expectedAuth);

            var existingUsers = userRepository.findAll();
            assertThat(existingUsers).hasSize(1);
            var existingUser = existingUsers.getFirst();
            assertThat(existingUser.getUserId()).isSameAs(auth.getUserId());
            assertThat(existingUser.getPassword()).isNotEqualTo(registrationDto.getPassword());

            var jwt = jwtDecoder.decode(auth.getToken());
            assertThat(jwt.getSubject()).isEqualTo(auth.getUserId().toString());
        }

        @ParameterizedTest
        @MethodSource("existingUsernameRegistrationDtos")
        void shouldNotRegisterWhenUsernameIsTaken(RegistrationDto registrationDto) {
            var user = UserEntity.builder()
                    .username(UserFixtures.USERNAME)
                    .email(UserFixtures.EMAIL)
                    .password(UserFixtures.HASHED_PASSWORD)
                    .build();
            userRepository.save(user);

            var result = client.post()
                    .uri(URI)
                    .body(registrationDto)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ProblemDetailsDto.class);

            var response = result.returnResult().getResponseBody();
            var existingUsers = userRepository.findAll();
            var expectedProblemDetailField = new ProblemDetailsFieldDto();
            expectedProblemDetailField.setError("validation.emailTaken");

            var expectedProblem = new ProblemDetailsDto();
            expectedProblem.setTitle("Conflict");
            expectedProblem.setDetail("Uer already exists");
            expectedProblem.setStatus(HttpStatus.CONFLICT.value());
            expectedProblem.setFields(Map.of("username", expectedProblemDetailField));

            assertThat(response).isNotNull();
            assertThat(response).isEqualTo(expectedProblem);
            assertThat(existingUsers).hasSize(1);
        }

        @ParameterizedTest
        @MethodSource("existingEmailRegistrationDtos")
        void shouldNotRegisterWhenEmailIsTaken(RegistrationDto registrationDto) {
            var user = UserEntity.builder()
                    .username(UserFixtures.USERNAME)
                    .email(UserFixtures.EMAIL)
                    .password(UserFixtures.HASHED_PASSWORD)
                    .build();
            userRepository.save(user);

            var result = client.post()
                    .uri(URI)
                    .body(registrationDto)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                    .expectBody(ProblemDetailsDto.class);

            var response = result.returnResult().getResponseBody();
            var existingUsers = userRepository.findAll();

            var expectedProblemField = new ProblemDetailsFieldDto();
            expectedProblemField.setError("validation.emailTaken");

            var expectedProblem = new ProblemDetailsDto();
            expectedProblem.setTitle("Conflict");
            expectedProblem.setDetail("User already exists");
            expectedProblem.setStatus(HttpStatus.CONFLICT.value());
            expectedProblem.setFields(Map.of("email", expectedProblemField));

            assertThat(response).isNotNull();
            assertThat(response).isEqualTo(expectedProblem);
            assertThat(existingUsers).hasSize(1);
        }

        @ParameterizedTest
        @MethodSource("invalidRegistrationDtos")
        void shouldNotRegisterWhenInvalidRegistrationDtoIsGiven(RegistrationDto registrationDto, String invalidFieldName) {
            var result = client.post()
                    .uri(URI)
                    .body(registrationDto)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                    .expectBody(ProblemDetailsDto.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Bad Request");
            assertThat(result.getFields()).containsKey(invalidFieldName);

            var invalidField = result.getFields().get(invalidFieldName);
            assertThat(invalidField).isNotNull();
            assertThat(invalidField.getError()).isNotEmpty();
        }
    }

    @Nested
    public class Login {
        private static final String URI = "/api/auth/login";

        public static Stream<Arguments> validCredentials() {
            return Stream.of(
                    Arguments.of(new CredentialsDto(UserFixtures.USERNAME, AuthFixtures.PLAIN_PASSWORD)),
                    Arguments.of(new CredentialsDto(UserFixtures.USERNAME.toUpperCase(), AuthFixtures.PLAIN_PASSWORD)),
                    Arguments.of(new CredentialsDto(UserFixtures.EMAIL, AuthFixtures.PLAIN_PASSWORD)),
                    Arguments.of(new CredentialsDto(UserFixtures.EMAIL.toUpperCase(), AuthFixtures.PLAIN_PASSWORD))
            );
        }

        public static Stream<Arguments> invalidPasswordCredentials() {
            return Stream.of(
                    Arguments.of(new CredentialsDto(UserFixtures.USERNAME, "wrong-password")),
                    Arguments.of(new CredentialsDto(UserFixtures.USERNAME, " " + AuthFixtures.PLAIN_PASSWORD)),
                    Arguments.of(new CredentialsDto(UserFixtures.USERNAME, AuthFixtures.PLAIN_PASSWORD + " ")),
                    Arguments.of(new CredentialsDto(UserFixtures.USERNAME, AuthFixtures.PLAIN_PASSWORD.toUpperCase())),
                    Arguments.of(new CredentialsDto(UserFixtures.USERNAME, AuthFixtures.PLAIN_PASSWORD.repeat(100)), "password")
            );
        }

        public static Stream<Arguments> malformedCredentials() {
            return Stream.of(
                    Arguments.of(new CredentialsDto("", AuthFixtures.PLAIN_PASSWORD), "identifier"),
                    Arguments.of(new CredentialsDto(UserFixtures.USERNAME, ""), "password"),
                    Arguments.of(new CredentialsDto(null, AuthFixtures.PLAIN_PASSWORD), "identifier"),
                    Arguments.of(new CredentialsDto(UserFixtures.USERNAME, null), "password"),
                    Arguments.of(new CredentialsDto(UserFixtures.USERNAME.repeat(100), AuthFixtures.PLAIN_PASSWORD), "identifier")
            );
        }

        @ParameterizedTest
        @MethodSource("validCredentials")
        void shouldLoginWithValidUsernameCredentials(CredentialsDto credentialsDto) {
            var existingUser = UserEntity.builder()
                    .username(UserFixtures.USERNAME)
                    .email(UserFixtures.EMAIL)
                    .password(passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD))
                    .build();
            userRepository.save(existingUser);

            var result = client.post().uri(URI)
                    .body(credentialsDto)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(AuthDto.class)
                    .returnResult()
                    .getResponseBody();

            var expectedAuth = new AuthDto(existingUser.getUserId(), "token", new UserDto(existingUser.getUserId(), UserFixtures.USERNAME, UserFixtures.EMAIL));

            assertThat(result).isNotNull();
            assertThat(result).usingRecursiveComparison().ignoringFields("token").isEqualTo(expectedAuth);
            assertThat(result.getToken()).isNotEmpty();

            var jwt = jwtDecoder.decode(result.getToken());
            assertThat(jwt.getSubject()).isEqualTo(result.getUserId().toString());
        }

        @ParameterizedTest
        @MethodSource("validCredentials")
        void shouldLoginWithValidEmailCredentials(CredentialsDto credentialsDto) {
            var existingUser = UserEntity.builder()
                    .username(UserFixtures.USERNAME)
                    .email(UserFixtures.EMAIL)
                    .password(passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD))
                    .build();
            userRepository.save(existingUser);

            var result = client.post().uri(URI)
                    .body(credentialsDto)
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
                    .expectBody(ProblemDetailsDto.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(result).isNotNull();
            assertThat(result.getDetail()).isEqualTo("Invalid credentials");
        }

        @ParameterizedTest
        @MethodSource("invalidPasswordCredentials")
        void shouldRejectLoginWithWrongPassword(CredentialsDto credentialsDto) {
            var existingUser = UserEntity.builder()
                    .username(UserFixtures.USERNAME)
                    .email(UserFixtures.EMAIL)
                    .password(passwordEncoder.encode(AuthFixtures.PLAIN_PASSWORD))
                    .build();
            userRepository.save(existingUser);

            var result = client.post().uri(URI)
                    .body(credentialsDto)
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody(ProblemDetailsDto.class)
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
                    .expectBody(ProblemDetailsDto.class)
                    .returnResult()
                    .getResponseBody();

            var wrongPasswordResult = client.post().uri(URI)
                    .body(new CredentialsDto(UserFixtures.USERNAME, "wrong-password"))
                    .exchange()
                    .expectBody(ProblemDetailsDto.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(wrongIdentifierResult).isNotNull();
            assertThat(wrongPasswordResult).isNotNull();
            assertThat(wrongIdentifierResult).isEqualTo(wrongPasswordResult);
        }

        @ParameterizedTest
        @MethodSource("malformedCredentials")
        void shouldRejectLoginWithMalformedCredentials(CredentialsDto credentialsDto, String invalidFieldName) {
            var result = client.post().uri(URI).body(credentialsDto).exchange().expectStatus().isBadRequest().expectBody(ProblemDetailsDto.class).returnResult().getResponseBody();

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Bad Request");
            assertThat(result.getFields()).containsKey(invalidFieldName);

            var invalidField = result.getFields().get(invalidFieldName);
            assertThat(invalidField).isNotNull();
            assertThat(invalidField.getError()).isNotEmpty();
        }
    }
}
