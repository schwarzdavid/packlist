package app.schwarzdavid.packlist.user.service.domain.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
}
