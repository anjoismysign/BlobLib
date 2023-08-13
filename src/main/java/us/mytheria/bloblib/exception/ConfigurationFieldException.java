package us.mytheria.bloblib.exception;

public class ConfigurationFieldException extends RuntimeException {
    public ConfigurationFieldException(String message) {
        super(message);
    }

    public ConfigurationFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationFieldException(Throwable cause) {
        super(cause);
    }
}
