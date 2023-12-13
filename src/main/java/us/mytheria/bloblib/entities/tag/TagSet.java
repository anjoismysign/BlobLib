package us.mytheria.bloblib.entities.tag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.BlobLibTagAPI;
import us.mytheria.bloblib.entities.DataAsset;
import us.mytheria.bloblib.entities.DataAssetType;

import java.util.Objects;
import java.util.Set;

public record TagSet(@NotNull Set<String> getInclusions,
                     @NotNull String getReference) implements DataAsset {

    /**
     * Gets a TagSet by its key. Key is the same as getReference.
     *
     * @param key The key to get the tag set by.
     * @return The TagSet, or null if it doesn't exist.
     */
    @Nullable
    public static TagSet by(@NotNull String key) {
        Objects.requireNonNull(key);
        return BlobLibTagAPI.getInstance().getTagSet(key);
    }

    public DataAssetType getType() {
        return DataAssetType.TAG_SET;
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
