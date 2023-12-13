package us.mytheria.bloblib.entities;

import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface IFileManager {
    /**
     * Gets the directory for the given data asset type.
     *
     * @param type The data asset type.
     * @return The directory for the given data asset type.
     */
    @Nullable
    File getDirectory(DataAssetType type);

    @Deprecated
    File messagesDirectory();

    @Deprecated
    File soundsDirectory();

    @Deprecated
    File inventoriesDirectory();

    @Deprecated
    File metaInventoriesDirectory();

    @Deprecated
    File actionsDirectory();

    @Deprecated
    File translatableSnippetsDirectory();

    @Deprecated
    File translatableBlocksDirectory();
}
