package app.schwarzdavid.packlist.common.util;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpenApiResponderTest {
    @Mock
    private RoutingContext routingContext;

    @Mock
    private HttpServerResponse response;

    @Mock
    private Function<RoutingContext, Future<Object>> function;

    @BeforeEach
    public void setup() {
        lenient().when(routingContext.response()).thenReturn(response);
    }

    @Test
    public void shouldCallFunctionAndSendResponse() {
        when(function.apply(any(RoutingContext.class))).thenReturn(Future.succeededFuture(JsonObject.of("foo", "bar")));
        when(response.headWritten()).thenReturn(false);

        OpenApiResponder.respond(function).handle(routingContext);

        verify(routingContext).json(any(JsonObject.class));
        verify(response, never()).end();
        verify(response, never()).setStatusCode(anyInt());
        verify(routingContext, never()).fail(any());
    }

    @Test
    public void shouldSendNoContentStatusWhenBodyIsEmpty() {
        when(function.apply(any(RoutingContext.class))).thenReturn(Future.succeededFuture(null));
        when(response.headWritten()).thenReturn(false);
        when(response.setStatusCode(anyInt())).thenReturn(response);

        OpenApiResponder.respond(function).handle(routingContext);

        verify(response).setStatusCode(204);
        verify(response).end();
        verify(routingContext, never()).json(any());
        verify(routingContext, never()).fail(any());
    }

    @Test
    public void shouldEndResponseWhenBodyIsEmptyAndHeadersAlreadySent() {
        when(function.apply(any(RoutingContext.class))).thenReturn(Future.succeededFuture(null));
        when(response.headWritten()).thenReturn(true);
        when(response.ended()).thenReturn(false);

        OpenApiResponder.respond(function).handle(routingContext);

        verify(response, never()).setStatusCode(anyInt());
        verify(routingContext, never()).json(any());
        verify(routingContext, never()).fail(any());
        verify(response).end();
    }

    @Test
    public void shouldDoNothingWhenBodyIsEmptyAndRequestHasEnded() {
        when(function.apply(any(RoutingContext.class))).thenReturn(Future.succeededFuture(null));
        when(response.headWritten()).thenReturn(true);
        when(response.ended()).thenReturn(true);

        OpenApiResponder.respond(function).handle(routingContext);

        verify(response, never()).setStatusCode(anyInt());
        verify(response, never()).end();
        verify(routingContext, never()).json(any());
        verify(routingContext, never()).fail(any());
    }

    @Test
    public void shouldFailWhenBodyIsSetAndRequestHasEnded() {
        when(function.apply(any(RoutingContext.class))).thenReturn(Future.succeededFuture(JsonObject.of("foo", "bar")));
        when(response.headWritten()).thenReturn(true);

        OpenApiResponder.respond(function).handle(routingContext);

        verify(response, never()).setStatusCode(anyInt());
        verify(response, never()).end();
        verify(routingContext, never()).json(any());
        verify(routingContext).fail(assertArg(throwable -> {
            assertInstanceOf(HttpException.class, throwable);
            assertEquals(500, ((HttpException) throwable).getStatusCode());
            assertEquals("Response already written", ((HttpException) throwable).getPayload());
        }));
    }

    @Test
    public void shouldFailOnFailedFuture() {
        when(function.apply(any(RoutingContext.class))).thenReturn(Future.failedFuture(new RuntimeException("foo")));

        OpenApiResponder.respond(function).handle(routingContext);

        verifyNoInteractions(response);
        verify(routingContext, never()).json(any());
        verify(routingContext).fail(assertArg(throwable -> {
            assertInstanceOf(RuntimeException.class, throwable);
            assertEquals("foo", throwable.getMessage());
        }));
    }

    @Test
    public void shouldFailOnException() {
        when(function.apply(any(RoutingContext.class))).thenThrow(new RuntimeException("bar"));

        OpenApiResponder.respond(function).handle(routingContext);

        verifyNoInteractions(response);
        verify(routingContext, never()).json(any());
        verify(routingContext).fail(assertArg(throwable -> {
            assertInstanceOf(RuntimeException.class, throwable);
            assertEquals("bar", throwable.getMessage());
        }));
    }
}
