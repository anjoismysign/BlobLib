package io.github.anjoismysign.bloblib.exception;

import java.io.File;

public class InterpretationException extends RuntimeException {
    public static InterpretationException INVALID_ARGUMENT() {
        return new InterpretationException(ExceptionType.INVALID_ARGUMENT);
    }

    public static InterpretationException INVALID_ARGUMENT(String extra) {
        return new InterpretationException(ExceptionType.INVALID_ARGUMENT, extra);
    }

    public static InterpretationException INVALID_OPERATION() {
        return new InterpretationException(ExceptionType.INVALID_OPERATION);
    }

    public static InterpretationException INVALID_OPERATION(String extra) {
        return new InterpretationException(ExceptionType.INVALID_OPERATION, extra);
    }

    public static InterpretationException INVALID_INPUT() {
        return new InterpretationException(ExceptionType.INVALID_INPUT);
    }

    public static InterpretationException INVALID_INPUT(String extra) {
        return new InterpretationException(ExceptionType.INVALID_INPUT, extra);
    }

    public enum ExceptionType {
        INVALID_ARGUMENT("Invalid argument"),
        INVALID_OPERATION("Invalid operation"),
        INVALID_INPUT("Invalid input");
        private final String message;

        ExceptionType(String message) {
            this.message = message;
        }
    }

    public InterpretationException(ExceptionType exceptionType, String extra) {
        super(exceptionType.message + ": " + extra);
    }

    public InterpretationException(ExceptionType exceptionType) {
        super(exceptionType.message);
    }

    public InterpretationException(String message) {
        super(message);
    }

    public InterpretationException(String message, File file) {
        this(file == null ? message : message + " \n At: " + file.getPath());
    }

    public InterpretationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterpretationException(String message, File file, Throwable cause) {
        this(file == null ? message : message + " \n At: " + file.getPath(), cause);
    }

    public InterpretationException(Throwable cause) {
        super(cause);
    }
}
