package app.schwarzdavid.packlist.user.service.domain.auth;

import app.schwarzdavid.packlist.user.service.fixture.UserFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtService, "secretTTL", Duration.ofDays(10));
    }

    @Test
    void shouldGenerateJwt() {
        when(jwtEncoder.encode(any())).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("token");

        var userData = UserFixtures.userData();
        var token = jwtService.createJwtForUser(userData);
        var encoderParamsCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);

        verify(jwtEncoder).encode(encoderParamsCaptor.capture());

        var claims = encoderParamsCaptor.getValue().getClaims();

        assertThat(token).isEqualTo("token");
        assertThat(claims.getSubject()).isEqualTo(userData.userId().toString());
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiresAt()).isNotNull();
        assertThat(Duration.between(claims.getIssuedAt(), claims.getExpiresAt())).isEqualTo(Duration.ofDays(10));
    }
}
