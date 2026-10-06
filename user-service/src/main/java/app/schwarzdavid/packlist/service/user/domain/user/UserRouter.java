package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.common.JwtService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import io.vertx.ext.web.openapi.router.RequestExtractor;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.openapi.contract.OpenAPIContract;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

@RequiredArgsConstructor
public final class UserRouter {
    private final UserHandler userHandler;
    private final OpenAPIContract contract;
    private final JwtService jwtService;

    // Custom implementation of Router::respond since the OpenApiRouter does not support this yet
    private static <T> void respondHandler(RoutingContext ctx, Function<RoutingContext, Future<@Nullable T>> fn) {
        try {
            fn.apply(ctx)
                    .onFailure(ctx::fail)
                    .onSuccess(body -> {
                        if (!ctx.response().headWritten()) {
                            if (body == null) {
                                ctx.response().setStatusCode(204).end();
                                return;
                            }
                            boolean hasContentType = ctx.response().headers().contains(HttpHeaders.CONTENT_TYPE);
                            if (body instanceof Buffer bufferBody) {
                                if (!hasContentType) {
                                    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
                                }
                                ctx.end(bufferBody);
                                return;
                            }
                            if (body instanceof String stringBody) {
                                if (!hasContentType) {
                                    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/html");
                                }
                                ctx.end(stringBody);
                                return;
                            }
                            ctx.json(body);
                        } else {
                            if (body == null) {
                                if (!ctx.response().ended()) {
                                    ctx.response().end();
                                }
                            } else {
                                ctx.fail(new HttpException(500, "Response already written"));
                            }
                        }
                    });
        } catch (RuntimeException e) {
            ctx.fail(e);
        }
    }

    public Router createRouter(Vertx vertx) {
        var routerBuilder = RouterBuilder.create(vertx, contract, RequestExtractor.withBodyHandler());

        routerBuilder.security("bearerAuth").httpHandler(jwtService.getHandler());

        routerBuilder.getRoute("createUser").addHandler(ctx -> respondHandler(ctx, userHandler::createUser));

        return routerBuilder.createRouter();
    }
}
