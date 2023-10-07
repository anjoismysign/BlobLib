package us.mytheria.bloblib.entities.proxy;

import us.mytheria.bloblib.entities.IFileManager;

import java.io.File;

public class IFileManagerProxy implements IFileManager {
    private final IFileManager fileManager;

    protected IFileManagerProxy(IFileManager fileManager) {
        this.fileManager = fileManager;
    }

    public File messagesDirectory() {
        return fileManager.messagesDirectory();
    }

    public File soundsDirectory() {
        return fileManager.soundsDirectory();
    }

    public File inventoriesDirectory() {
        return fileManager.inventoriesDirectory();
    }

    public File metaInventoriesDirectory() {
        return fileManager.metaInventoriesDirectory();
    }

    public File actionsDirectory() {
        return fileManager.actionsDirectory();
    }

    public File translatableSnippetsDirectory() {
        return fileManager.translatableSnippetsDirectory();
    }

    public File translatableBlocksDirectory() {
        return fileManager.translatableBlocksDirectory();
    }
}
