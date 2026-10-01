package app.schwarzdavid.packlist.common.exception;

import app.schwarzdavid.packlist.common.exception.api.ApiException;
import io.vertx.core.internal.logging.Logger;
import io.vertx.core.internal.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;

public class RouterExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RouterExceptionHandler.class);

    private RouterExceptionHandler() {
    }

    public static void handleFailure(RoutingContext ctx) {
        Throwable throwable = ctx.failure();

        if(throwable instanceof ApiException apiException) {
            logger.error("Handling ApiException", throwable);
            ctx.response().setStatusCode(apiException.getStatusCode()).end(apiException.getMessage());
            return;
        }

        if (throwable instanceof HttpException httpException) {
            logger.error("Handling HttpException", throwable);
            var content = httpException.getPayload();
            if (content == null) {
                content = httpException.getMessage();
            }
            ctx.response().setStatusCode(httpException.getStatusCode()).end(content);
            return;
        }

        logger.error("Handling unexpected exception", throwable);
        ctx.response().setStatusCode(500).end("Internal Server Error");
    }
}
