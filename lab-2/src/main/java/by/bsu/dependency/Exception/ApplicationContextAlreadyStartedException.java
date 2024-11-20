package by.bsu.dependency.Exception;

public class ApplicationContextAlreadyStartedException extends RuntimeException {
    public ApplicationContextAlreadyStartedException(String message) {
        super(message);
    }
}
