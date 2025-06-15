package io.github.anjoismysign.bloblib.entities.translatable;

import io.github.anjoismysign.bloblib.api.BlobLibTranslatableAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public interface TranslatableBlock extends Translatable<List<String>> {

    /**
     * Gets a TranslatableBlock by its key. Key is the same as identifier.
     *
     * @param key The key to get the tag set by.
     * @return The TranslatableBlock, or null if it doesn't exist.
     */
    @Nullable
    static TranslatableBlock by(@NotNull String key) {
        Objects.requireNonNull(key);
        return BlobLibTranslatableAPI.getInstance().getTranslatableBlock(key);
    }

    default TranslatableBlockModder modder() {
        return TranslatableBlockModder.mod(this);
    }
}
