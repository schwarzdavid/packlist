package app.schwarzdavid.packlist.user.service.domain.auth.exception;

public class InvalidCredentialsException extends RuntimeException {
    private final String message = "Invalid Credentials";

    public InvalidCredentialsException() {
        super("Invalid Credentials");
    }
}
