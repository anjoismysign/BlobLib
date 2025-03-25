package us.mytheria.bloblib.entities.translatable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;

import java.util.Objects;

public interface TranslatableSnippet extends Translatable<String> {

    /**
     * Gets a TranslatableSnippet by its key. Key is the same as identifier.
     *
     * @param key The key to get the tag set by.
     * @return The TranslatableSnippet, or null if it doesn't exist.
     */
    @Nullable
    static TranslatableSnippet by(@NotNull String key) {
        Objects.requireNonNull(key);
        return BlobLibTranslatableAPI.getInstance().getTranslatableSnippet(key);
    }

    default TranslatableSnippetModder modder() {
        return TranslatableSnippetModder.mod(this);
    }
}
