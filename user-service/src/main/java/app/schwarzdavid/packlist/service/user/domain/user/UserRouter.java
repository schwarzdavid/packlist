package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.common.JwtService;
import app.schwarzdavid.packlist.service.user.infrastructure.util.OpenApiResponder;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.router.RequestExtractor;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.openapi.contract.OpenAPIContract;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class UserRouter {
    private final UserHandler userHandler;
    private final OpenAPIContract contract;
    private final JwtService jwtService;



    public Router createRouter(Vertx vertx) {
        var routerBuilder = RouterBuilder.create(vertx, contract, RequestExtractor.withBodyHandler());

        routerBuilder.security("bearerAuth").httpHandler(jwtService.getHandler());

        routerBuilder.getRoute("createUser").addHandler(ctx -> OpenApiResponder.respond(ctx, userHandler::createUser));

        return routerBuilder.createRouter();
    }
}
