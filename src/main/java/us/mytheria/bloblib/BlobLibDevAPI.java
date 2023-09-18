package us.mytheria.bloblib;

import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.api.BlobLibInventoryAPI;
import us.mytheria.bloblib.api.BlobLibMessageAPI;
import us.mytheria.bloblib.api.BlobLibSoundAPI;

import java.io.File;

/**
 * @author anjoismysign
 * It's meant to hold quick/static methods that later are meant to be
 * moved to a different, singleton pattern class.
 * Consider all methods as deprecated and subject to change.
 */
@Deprecated
public class BlobLibDevAPI {

    /**
     * @deprecated Use {@link BlobLibMessageAPI#getMessagesDirectory()} instead
     * to avoid confusion.
     */
    @Deprecated
    @NotNull
    public static File getMessagesFile() {
        throw new UnsupportedOperationException("Use BlobLibMessageAPI instead");
    }

    /**
     * @deprecated Use {@link BlobLibSoundAPI#getSoundsDirectory()} instead
     * to avoid confusion.
     */
    @Deprecated
    @NotNull
    public static File getSoundsFile() {
        throw new UnsupportedOperationException("Use BlobLibSoundAPI instead");
    }

    /**
     * @deprecated Use {@link BlobLibInventoryAPI#getInventoriesDirectory()} instead
     * to avoid confusion.
     */
    @Deprecated
    @NotNull
    public static File getInventoriesFile() {
        throw new UnsupportedOperationException("Use BlobLibInventoryAPI instead");
    }
}
