package pl.sda.refactorapp.service.exception;

public final class InvalidOrderItemsException extends DomainException {

    public InvalidOrderItemsException(String message) {
        super(message);
    }

    public InvalidOrderItemsException(String message, Throwable cause) {
        super(message, cause);
    }
}
