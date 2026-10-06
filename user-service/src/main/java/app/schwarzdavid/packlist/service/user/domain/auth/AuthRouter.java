package app.schwarzdavid.packlist.service.user.domain.auth;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.validation.RequestPredicate;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Bodies;
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder;
import io.vertx.json.schema.SchemaRepository;
import io.vertx.json.schema.common.dsl.StringFormat;
import lombok.RequiredArgsConstructor;

import static io.vertx.json.schema.common.dsl.Keywords.format;
import static io.vertx.json.schema.common.dsl.Keywords.minLength;
import static io.vertx.json.schema.common.dsl.Schemas.objectSchema;
import static io.vertx.json.schema.common.dsl.Schemas.stringSchema;

@RequiredArgsConstructor
public class AuthRouter {
    private final AuthHandler authHandler;
    private final SchemaRepository schemaRepository;

    public Router createRouter(Vertx vertx) {
        var router = Router.router(vertx);

        router.post("/api/auth/login")
                .handler(validateLogin())
                .respond(authHandler::login);

        router.post("/api/auth/register")
                .handler(validateRegister())
                .respond(authHandler::register);

        return router;
    }

    private ValidationHandler validateLogin() {
        var bodySchema = objectSchema()
                .requiredProperty("identifier", stringSchema().with(minLength(1)))
                .requiredProperty("password", stringSchema().with(minLength(1)));

        return ValidationHandlerBuilder.create(schemaRepository)
                .predicate(RequestPredicate.BODY_REQUIRED)
                .body(Bodies.json(bodySchema))
                .build();
    }

    private ValidationHandler validateRegister() {
        var bodySchema = objectSchema()
                .requiredProperty("username", stringSchema().with(minLength(1)))
                .requiredProperty("email", stringSchema().with(format(StringFormat.EMAIL)))
                .requiredProperty("password", stringSchema().with(minLength(8)));

        return ValidationHandlerBuilder.create(schemaRepository)
                .predicate(RequestPredicate.BODY_REQUIRED)
                .body(Bodies.json(bodySchema))
                .build();
    }
}
