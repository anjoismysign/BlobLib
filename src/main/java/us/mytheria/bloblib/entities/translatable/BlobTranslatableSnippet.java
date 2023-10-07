package us.mytheria.bloblib.entities.translatable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public class BlobTranslatableSnippet implements TranslatableSnippet {
    @NotNull
    private final String locale, snippet;

    public static BlobTranslatableSnippet of(@NotNull String locale,
                                             @NotNull String snippet) {
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(snippet, "Snippet cannot be null");
        return new BlobTranslatableSnippet(locale, snippet);
    }

    private BlobTranslatableSnippet(@NotNull String locale,
                                    @NotNull String snippet) {
        this.locale = locale;
        this.snippet = snippet;
    }

    @Override
    @NotNull
    public String getLocale() {
        return locale;
    }

    @NotNull
    public String get() {
        return snippet;
    }

    @Override
    @NotNull
    public TranslatableSnippet modify(Function<String, String> function) {
        return new BlobTranslatableSnippet(locale, function.apply(snippet));
    }
}
