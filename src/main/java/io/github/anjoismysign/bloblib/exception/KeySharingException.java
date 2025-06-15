package io.github.anjoismysign.bloblib.exception;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class KeySharingException extends RuntimeException {
    public static KeySharingException DEFAULT(@NotNull String key) {
        Objects.requireNonNull(key);
        return new KeySharingException("The key " + key + " is already in use");
    }

    public KeySharingException(String message) {
        super(message);
    }
}
