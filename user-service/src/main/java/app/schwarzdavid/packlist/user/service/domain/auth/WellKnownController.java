package app.schwarzdavid.packlist.user.service.domain.auth;

import com.nimbusds.jose.jwk.JWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class WellKnownController {
    private final JWKSet jwkSet;

    @GetMapping("/.well-known/jwks.json")
    ResponseEntity<Map<?, ?>> getJwks() {
        return ResponseEntity.ok(jwkSet.toPublicJWKSet().toJSONObject());
    }
}
