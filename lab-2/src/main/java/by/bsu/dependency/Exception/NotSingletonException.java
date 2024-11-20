package by.bsu.dependency.Exception;

public class NotSingletonException extends RuntimeException {
    public NotSingletonException(String message) {
        super(message);
    }
}
