package io.github.anjoismysign.bloblib.entities.proxy;

import io.github.anjoismysign.bloblib.entities.DataAssetType;
import io.github.anjoismysign.bloblib.entities.IFileManager;
import org.jetbrains.annotations.NotNull;

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
