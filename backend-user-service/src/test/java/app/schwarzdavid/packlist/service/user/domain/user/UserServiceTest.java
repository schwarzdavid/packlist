package app.schwarzdavid.packlist.service.user.domain.user;

import app.schwarzdavid.packlist.common.exception.db.UniqueConstraintValidationException;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserCreateData;
import app.schwarzdavid.packlist.service.user.domain.user.data.UserData;
import app.schwarzdavid.packlist.service.user.domain.user.exception.UserAlreadyExistsException;
import app.schwarzdavid.packlist.service.user.fixture.UserFixtures;
import app.schwarzdavid.packlist.service.user.service.security.PasswordHashingService;
import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHashingService passwordHashingService;

    @InjectMocks
    private UserService userService;

    @Test
    public void shouldCreateUser(VertxTestContext ctx) {
        when(passwordHashingService.hashPassword(eq(UserFixtures.PLAIN_PASSWORD))).thenReturn(Future.succeededFuture(UserFixtures.HASHED_PASSWORD));
        when(userRepository.createUser(any(UserCreateData.class), eq(UserFixtures.HASHED_PASSWORD))).thenReturn(Future.succeededFuture(UserFixtures.userData()));

        ctx.assertComplete(userService.createUser(UserFixtures.create()))
                .onSuccess(user -> ctx.verify(() -> {
                    assertInstanceOf(UserData.class, user);
                    assertEquals(UserFixtures.ID, user.userId());
                    assertEquals(UserFixtures.USERNAME, user.username());
                    assertEquals(UserFixtures.EMAIL, user.email());

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldThrowCorrectExceptionWhenUsernameAlreadyExists(VertxTestContext ctx) {
        when(passwordHashingService.hashPassword(eq(UserFixtures.PLAIN_PASSWORD))).thenReturn(Future.succeededFuture(UserFixtures.HASHED_PASSWORD));
        when(userRepository.createUser(any(UserCreateData.class), eq(UserFixtures.HASHED_PASSWORD))).thenReturn(Future.failedFuture(new UniqueConstraintValidationException("uq_users_username", "foo")));

        ctx.assertFailure(userService.createUser(UserFixtures.create()))
                .onFailure(throwable -> ctx.verify(() -> {
                    assertInstanceOf(UserAlreadyExistsException.class, throwable);
                    assertEquals(UserAlreadyExistsException.Field.USERNAME, ((UserAlreadyExistsException) throwable).getField());

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldThrowCorrectExceptionWhenEmailAlreadyExists(VertxTestContext ctx) {
        when(passwordHashingService.hashPassword(eq(UserFixtures.PLAIN_PASSWORD))).thenReturn(Future.succeededFuture(UserFixtures.HASHED_PASSWORD));
        when(userRepository.createUser(any(UserCreateData.class), eq(UserFixtures.HASHED_PASSWORD))).thenReturn(Future.failedFuture(new UniqueConstraintValidationException("uq_users_email", "foo")));

        ctx.assertFailure(userService.createUser(UserFixtures.create()))
                .onFailure(throwable -> ctx.verify(() -> {
                    assertInstanceOf(UserAlreadyExistsException.class, throwable);
                    assertEquals(UserAlreadyExistsException.Field.EMAIL, ((UserAlreadyExistsException) throwable).getField());

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldThrowOriginalExceptionWhenUnknownConstraintFails(VertxTestContext ctx) {
        when(passwordHashingService.hashPassword(eq(UserFixtures.PLAIN_PASSWORD))).thenReturn(Future.succeededFuture(UserFixtures.HASHED_PASSWORD));
        when(userRepository.createUser(any(UserCreateData.class), eq(UserFixtures.HASHED_PASSWORD))).thenReturn(Future.failedFuture(new UniqueConstraintValidationException("unknown_constraint", "foo")));

        ctx.assertFailure(userService.createUser(UserFixtures.create()))
                .onFailure(throwable -> ctx.verify(() -> {
                    assertInstanceOf(UniqueConstraintValidationException.class, throwable);

                    ctx.completeNow();
                }));
    }

    @Test
    public void shouldThrowExceptionIfPasswordHashingFails(VertxTestContext ctx) {
        when(passwordHashingService.hashPassword(eq(UserFixtures.PLAIN_PASSWORD))).thenReturn(Future.failedFuture(new RuntimeException("foo")));

        ctx.assertFailure(userService.createUser(UserFixtures.create()))
                .onFailure(throwable -> ctx.verify(() -> {
                    assertInstanceOf(RuntimeException.class, throwable);

                    ctx.completeNow();
                }));
    }
}
