package us.mytheria.bloblib.entities.translatable;

import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.entities.DataAssetType;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlobTranslatableSnippet implements TranslatableSnippet {
    private static final Pattern pattern = Pattern.compile("\\{r=(.*?)}");

    public static String PARSE(@NotNull String text,
                               @NotNull String locale) {
        Objects.requireNonNull(text, "Text cannot be null");
        Objects.requireNonNull(locale, "Locale cannot be null");
        Matcher matcher = pattern.matcher(text);
        StringBuilder replaced = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            TranslatableSnippet result = BlobLibTranslatableAPI.getInstance()
                    .getTranslatableSnippet(key, locale);
            String replacement = result == null ? "" : result
                    .get();
            matcher.appendReplacement(replaced, replacement);
        }
        matcher.appendTail(replaced);
        return replaced.toString();
    }

    public static String PARSE(@NotNull String text) {
        return PARSE(text, "en_us");
    }

    @NotNull
    private final String locale, snippet, key;

    public static BlobTranslatableSnippet of(@NotNull String key,
                                             @NotNull String locale,
                                             @NotNull String snippet) {
        Objects.requireNonNull(locale, "Locale cannot be null");
        Objects.requireNonNull(snippet, "Snippet cannot be null");
        return new BlobTranslatableSnippet(key, locale, snippet);
    }

    private BlobTranslatableSnippet(@NotNull String key,
                                    @NotNull String locale,
                                    @NotNull String snippet) {
        this.key = key;
        this.locale = locale;
        this.snippet = PARSE(snippet, locale);
    }

    @NotNull
    public String getLocale() {
        return locale;
    }

    @NotNull
    public String get() {
        return snippet;
    }

    @NotNull
    public String getReference() {
        return key;
    }

    public DataAssetType getType() {
        return DataAssetType.TRANSLATABLE_SNIPPET;
    }

    @NotNull
    public TranslatableSnippet modify(Function<String, String> function) {
        return new BlobTranslatableSnippet(key, locale, function.apply(snippet));
    }
}
