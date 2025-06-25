package io.github.anjoismysign.bloblib.entities.translatable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A registry for Translatable objects.
 * Holds a default locale and a map of locales to Translatable objects.
 *
 * @param <T> the type of Translatable
 */
public class TranslatableRegistry<T extends Translatable<?>> {
    @NotNull
    private final String defaultLocale, key;
    private final Map<String, T> translatables;

    /**
     * Will instantiate a new TranslatableRegistry with the specified default locale.
     *
     * @param defaultLocale the default locale
     * @param <T>           the type of Translatable
     * @return a new TranslatableRegistry with the specified default locale
     */
    public static <T extends Translatable<?>> TranslatableRegistry<T> of(
            @NotNull String defaultLocale, @NotNull String key) {
        Objects.requireNonNull(defaultLocale, "'defaultLocale' cannot be null!");
        Objects.requireNonNull(key, "'key' cannot be null!");
        return new TranslatableRegistry<>(defaultLocale, key);
    }

    private TranslatableRegistry(@NotNull String defaultLocale, @NotNull String key) {
        this.defaultLocale = defaultLocale;
        this.key = key;
        this.translatables = new HashMap<>();
    }

    /**
     * Will process the Translatable and add it to the registry.
     *
     * @param translatable the Translatable to process
     * @return true if the Translatable was added, false if it already exists
     */
    public boolean process(@NotNull T translatable) {
        Objects.requireNonNull(translatable, "carrier cannot be null");
        String locale = translatable.locale();
        if (this.translatables.containsKey(locale))
            return false;
        this.translatables.put(locale, translatable);
        return true;
    }

    /**
     * Will get the Translatable for the specified locale, or the default if it doesn't exist.
     *
     * @param locale the locale to get the Translatable for
     * @return the Translatable for the specified locale, or the default if it doesn't exist
     */
    @Nullable
    public T get(String locale) {
        T result = this.translatables.get(locale);
        if (result == null)
            result = getDefault();
        return result;
    }

    /**
     * Will get the default Translatable.
     *
     * @return the Translatable carrier
     */
    @NotNull
    public T getDefault() {
        return this.translatables.get(this.defaultLocale);
    }

    @NotNull
    public String getKey() {
        return key;
    }
}
