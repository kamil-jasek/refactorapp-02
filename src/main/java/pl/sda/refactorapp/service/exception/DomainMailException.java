package pl.sda.refactorapp.service.exception;

public final class DomainMailException extends DomainException {

    public DomainMailException(String message) {
        super(message);
    }

    public DomainMailException(String message, Throwable cause) {
        super(message, cause);
    }
}
