package us.mytheria.bloblib.entities;

import java.io.File;

public interface IFileManager {
    File messagesDirectory();

    File soundsDirectory();

    File inventoriesDirectory();

    File metaInventoriesDirectory();

    File actionsDirectory();
}
