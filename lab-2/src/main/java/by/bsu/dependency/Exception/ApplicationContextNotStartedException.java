package by.bsu.dependency.Exception;

public class ApplicationContextNotStartedException extends RuntimeException {
    public ApplicationContextNotStartedException(String message) {
        super(message);
    }
}
