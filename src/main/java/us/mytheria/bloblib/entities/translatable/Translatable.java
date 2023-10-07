package us.mytheria.bloblib.entities.translatable;

import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.entities.Localizable;

import java.util.function.Function;

public interface Translatable<T> extends Localizable {
    /**
     * Gets the translatable
     *
     * @return The translatable
     */
    @NotNull
    T get();

    /**
     * @param function The function to modify the translatable with
     * @return A new translatable with the modified message
     */
    @NotNull
    Translatable<T> modify(Function<String, String> function);
}
