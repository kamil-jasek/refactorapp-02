package pl.sda.refactorapp.service.exception;

public final class CustomerNotExistsException extends DomainException {

    public CustomerNotExistsException(String message) {
        super(message);
    }

    public CustomerNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
