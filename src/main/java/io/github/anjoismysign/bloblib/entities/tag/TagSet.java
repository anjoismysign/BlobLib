package io.github.anjoismysign.bloblib.entities.tag;

import io.github.anjoismysign.bloblib.api.BlobLibTagAPI;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public record TagSet(@NotNull Set<String> getInclusions,
                     @NotNull String identifier) implements DataAsset {

    /**
     * Gets a TagSet by its key. Key is the same as identifier.
     *
     * @param key The key to get the tag set by.
     * @return The TagSet, or null if it doesn't exist.
     */
    @Nullable
    public static TagSet by(@NotNull String key) {
        Objects.requireNonNull(key);
        return BlobLibTagAPI.getInstance().getTagSet(key);
    }

    /**
     * Checks if inclusions contains a tag.
     *
     * @param tag The tag to check for.
     * @return True if inclusions contains the tag, false otherwise
     */
    public boolean contains(@NotNull String tag) {
        Objects.requireNonNull(tag);
        return getInclusions.contains(tag);
    }
}
