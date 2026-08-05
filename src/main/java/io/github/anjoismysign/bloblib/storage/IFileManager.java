package io.github.anjoismysign.bloblib.storage;
import io.github.anjoismysign.bloblib.domain.DataAssetType;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface IFileManager {
    /**
     * Gets the directory for the given data asset type.
     *
     * @param type The data asset type.
     * @return The directory for the given data asset type.
     */
    @NotNull
    File getDirectory(DataAssetType type);
}
