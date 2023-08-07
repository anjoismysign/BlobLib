package us.mytheria.bloblib;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author anjoismysign
 * It's meant to hold quick/static methods that later are meant to be
 * moved to a different class, such as BlobLibAPI.
 * Consider all methods as deprecated and subject to change.
 * @deprecated Preparing rewrite for singleton pattern
 */
@Deprecated
public class BlobLibDevAPI {
    private static final BlobLib main = BlobLib.getInstance();

    /**
     * @return The messages file
     * @deprecated Use {@link BlobLibAssetAPI#getMessagesDirectory()} instead
     * to avoid confusion.
     */
    @Deprecated
    @NotNull
    public static File getMessagesFile() {
        return main.getFileManager().messagesDirectory();
    }

    /**
     * @return The sounds file
     * @deprecated Use {@link BlobLibAssetAPI#getSoundsDirectory()} instead
     * to avoid confusion.
     */
    @Deprecated
    @NotNull
    public static File getSoundsFile() {
        return main.getFileManager().soundsDirectory();
    }

    /**
     * Retrieves a file from the inventories' directory.
     *
     * @return The inventories file
     * @deprecated Use {@link BlobLibAssetAPI#getInventoriesDirectory()} instead
     * to avoid confusion.
     */
    @Deprecated
    @NotNull
    public static File getInventoriesFile() {
        return main.getFileManager().inventoriesDirectory();
    }
}
