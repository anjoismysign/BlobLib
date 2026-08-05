package io.github.anjoismysign.bloblib.proxy;

import io.github.anjoismysign.bloblib.domain.DataAssetType;
import io.github.anjoismysign.bloblib.storage.IFileManager;
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
