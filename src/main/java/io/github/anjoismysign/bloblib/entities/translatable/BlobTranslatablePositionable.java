package io.github.anjoismysign.bloblib.entities.translatable;

import io.github.anjoismysign.bloblib.entities.positionable.Positionable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public class BlobTranslatablePositionable implements TranslatablePositionable {

    @NotNull
    private final String locale, display, key;

    @NotNull
    private final Positionable positionable;

    public static BlobTranslatablePositionable of(@NotNull String key,
                                                  @NotNull String locale,
                                                  @NotNull String display,
                                                  @NotNull Positionable positionable) {
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(display, "Display cannot be null");
        Objects.requireNonNull(positionable, "Positionable cannot be null");
        return new BlobTranslatablePositionable(key, locale, display, positionable);
    }

    public BlobTranslatablePositionable(@NotNull String key,
                                        @NotNull String locale,
                                        @NotNull String display,
                                        @NotNull Positionable positionable) {
        this.key = key;
        this.locale = locale;
        this.display = BlobTranslatableSnippet.PARSE(display, locale);
        this.positionable = positionable;
    }

    @NotNull
    public String locale() {
        return locale;
    }

    @NotNull
    public Positionable get() {
        return positionable;
    }

    @NotNull
    public String identifier() {
        return key;
    }

    @NotNull
    public TranslatablePositionable modify(Function<String, String> function) {
        return new BlobTranslatablePositionable(key, locale, function.apply(display), positionable);
    }

    @NotNull
    public String getDisplay() {
        return display;
    }
}
