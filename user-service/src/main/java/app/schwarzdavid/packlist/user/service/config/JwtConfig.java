package app.schwarzdavid.packlist.user.service.config;

import app.schwarzdavid.packlist.user.service.config.properties.JwtProperties;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@EnableConfigurationProperties(JwtProperties.class)
@Configuration
@RequiredArgsConstructor
public class JwtConfig {
    private final JwtProperties jwtProperties;

    @Bean
    public JwtEncoder createJwtEncoderWithJwkSet(JWKSet jwkSet) {
        var jwkSource = new ImmutableJWKSet<>(jwkSet);
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    @Primary
    public JwtDecoder createJwtDecoder() throws IOException {
        return NimbusJwtDecoder.withPublicKey(getPublicKey()).build();
    }

    @Bean
    public JWKSet createJwtSet() throws IOException, JOSEException {
        var rsaKey = new RSAKey.Builder(getPublicKey()).privateKey(getPrivateKey()).keyIDFromThumbprint().build();
        return new JWKSet(rsaKey);
    }

    private RSAPublicKey getPublicKey() throws IOException {
        return RsaKeyConverters.x509().convert(jwtProperties.publicKey().getInputStream());
    }

    public RSAPrivateKey getPrivateKey() throws IOException {
        return RsaKeyConverters.pkcs8().convert(jwtProperties.privateKey().getInputStream());
    }
}
