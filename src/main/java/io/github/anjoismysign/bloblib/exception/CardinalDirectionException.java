package io.github.anjoismysign.bloblib.exception;

import java.io.File;

public class CardinalDirectionException extends RuntimeException {
    public CardinalDirectionException(String message) {
        super(message);
    }

    public CardinalDirectionException(String message, File file) {
        this(file == null ? message : message + " \n At: " + file.getPath());
    }

    public CardinalDirectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardinalDirectionException(String message, File file, Throwable cause) {
        this(file == null ? message : message + " \n At: " + file.getPath(), cause);
    }

    public CardinalDirectionException(Throwable cause) {
        super(cause);
    }
}
