package be.zsoft.jsitegen.exception;

public class WatchException extends RuntimeException {

    public WatchException(String message) {
        super(message);
    }

    public WatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
