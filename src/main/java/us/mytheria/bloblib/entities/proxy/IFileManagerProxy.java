package us.mytheria.bloblib.entities.proxy;

import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.entities.DataAssetType;
import us.mytheria.bloblib.entities.IFileManager;

import java.io.File;

public class IFileManagerProxy implements IFileManager {
    private final IFileManager fileManager;

    protected IFileManagerProxy(IFileManager fileManager) {
        this.fileManager = fileManager;
    }

    public @NotNull File getDirectory(DataAssetType type) {
        return fileManager.getDirectory(type);
    }
}
