package be.zsoft.jsitegen.exception;

public class GenerateNewException extends RuntimeException {

    public GenerateNewException(String message) {
        super(message);
    }

    public GenerateNewException(String message, Throwable cause) {
        super(message, cause);
    }
}
