package io.github.anjoismysign.bloblib.exception;

import java.io.File;

public class ConfigurationFieldException extends RuntimeException {
    public ConfigurationFieldException(String message) {
        super(message);
    }

    public ConfigurationFieldException(String message, File file) {
        this(file == null ? message : message + " \n At: " + file.getPath());
    }

    public ConfigurationFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationFieldException(String message, File file, Throwable cause) {
        this(file == null ? message : message + " \n At: " + file.getPath(), cause);
    }

    public ConfigurationFieldException(Throwable cause) {
        super(cause);
    }
}
