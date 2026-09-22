package app.schwarzdavid.packlist.service.user.domain.auth;

import app.schwarzdavid.packlist.service.user.domain.auth.dto.CredentialsDto;
import io.vertx.ext.web.RoutingContext;

public class AuthHandler {
    private final CredentialsService credentialsService;

    public AuthHandler(CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
    }

    public void login(RoutingContext ctx) {
        var credentials = ctx.body().asPojo(CredentialsDto.class);
    }
}
