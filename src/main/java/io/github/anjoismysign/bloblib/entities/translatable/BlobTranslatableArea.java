package io.github.anjoismysign.bloblib.entities.translatable;

import io.github.anjoismysign.bloblib.entities.area.Area;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public class BlobTranslatableArea implements TranslatableArea {

    @NotNull
    private final String locale, display, key;

    @NotNull
    private final Area area;

    public static BlobTranslatableArea of(@NotNull String key,
                                          @NotNull String locale,
                                          @NotNull String display,
                                          @NotNull Area area) {
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(display, "Display cannot be null");
        Objects.requireNonNull(area, "Area cannot be null");
        return new BlobTranslatableArea(key, locale, display, area);
    }

    BlobTranslatableArea(@NotNull String key,
                         @NotNull String locale,
                         @NotNull String display,
                         @NotNull Area area) {
        this.key = key;
        this.locale = locale;
        this.display = BlobTranslatableSnippet.PARSE(display, locale);
        this.area = area;
    }

    @NotNull
    public String locale() {
        return locale;
    }

    @NotNull
    public Area get() {
        return area;
    }

    @NotNull
    public String identifier() {
        return key;
    }

    @NotNull
    public TranslatableArea modify(Function<String, String> function) {
        return new BlobTranslatableArea(key, locale, function.apply(display), area);
    }

    @NotNull
    public String getDisplay() {
        return display;
    }
}
