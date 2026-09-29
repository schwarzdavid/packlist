package app.schwarzdavid.packlist.common.exception;

import app.schwarzdavid.packlist.common.exception.api.ApiException;
import io.vertx.ext.web.RoutingContext;

public class RouterExceptionHandler {
    private RouterExceptionHandler() {
    }

    public static void handleFailure(RoutingContext ctx) {
        Throwable throwable = ctx.failure();

        if(throwable instanceof ApiException apiException) {
            ctx.response().setStatusCode(apiException.getStatusCode()).end(apiException.getMessage());
            return;
        }

        ctx.response().setStatusCode(500).end("Internal Server Error");
    }
}
