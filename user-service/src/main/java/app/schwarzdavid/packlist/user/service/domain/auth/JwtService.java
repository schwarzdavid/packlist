package app.schwarzdavid.packlist.user.service.domain.auth;

import app.schwarzdavid.packlist.user.service.domain.user.data.UserData;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {
    private static final String DEFAULT_TTL_SEC = String.valueOf(60 * 60 * 24 * 31);

    private final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final Environment environment;
    private final Algorithm algorithm;

    public JwtService(Environment environment) {
        this.environment = environment;
        var secretKey = environment.getRequiredProperty("user-service.auth.jwt.secret");
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    public String generateToken(UserData userData) {
        return JWT.create()
                .withSubject(userData.userId().toString())
                .withIssuedAt(Instant.now())
                .withExpiresAt(getExpiresAt())
                .sign(algorithm);
    }

    private Instant getExpiresAt() {
        var secretTTL = environment.getProperty("user-service.auth.jwt.ttl_sec", DEFAULT_TTL_SEC);
        return Instant.now().plusSeconds(Long.parseLong(secretTTL));
    }
}
