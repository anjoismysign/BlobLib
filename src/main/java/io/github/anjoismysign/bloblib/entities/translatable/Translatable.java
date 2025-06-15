package io.github.anjoismysign.bloblib.entities.translatable;

import io.github.anjoismysign.bloblib.entities.Localizable;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface Translatable<T> extends DataAsset, Localizable {
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
