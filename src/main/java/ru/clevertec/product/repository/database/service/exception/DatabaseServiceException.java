package ru.clevertec.product.repository.database.service.exception;

public class DatabaseServiceException extends RuntimeException {
    public DatabaseServiceException() {
    }

    public DatabaseServiceException(String message) {
        super(message);
    }

    public DatabaseServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseServiceException(Throwable cause) {
        super(cause);
    }
}
