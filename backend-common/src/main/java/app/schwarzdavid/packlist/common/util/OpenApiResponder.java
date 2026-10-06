package app.schwarzdavid.packlist.common.util;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class OpenApiResponder {
    public static <T> Handler<RoutingContext> respond(Function<RoutingContext, Future<@Nullable T>> fn) {
        return (RoutingContext ctx) -> {
            try {
                fn.apply(ctx)
                        .onFailure(ctx::fail)
                        .onSuccess(body -> {
                            if (!ctx.response().headWritten()) {
                                if (body == null) {
                                    ctx.response().setStatusCode(204).end();
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
        };
    }
}
