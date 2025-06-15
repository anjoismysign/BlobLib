package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.bloblib.managers.BlobPlugin;
import io.github.anjoismysign.bloblib.managers.InventoryManager;
import io.github.anjoismysign.anjo.entities.NamingConventions;

import java.io.File;

public record ObjectDirectorData(String objectDirectory, String objectBuilderKey, String objectName) {

    /**
     * Constructs a new ObjectDirectorData. It will register
     * and update the object builder inventory asset.
     * You should use ObjectDirectorData.simple() instead.
     *
     * @param blobFileManager         The BlobFileManager
     * @param objectBuilderFilename   The filename of the object builder
     * @param objectDirectoryFilename The filename of the object directory
     * @param objectName              The name of the object
     * @return The ObjectDirectorData
     */
    public static ObjectDirectorData registerAndBuild(BlobFileManager blobFileManager,
                                                      String objectBuilderFilename,
                                                      String objectDirectoryFilename,
                                                      String objectName) {

        File file = new File(blobFileManager.getDirectory(DataAssetType.BLOB_INVENTORY) + "/" + objectBuilderFilename + ".yml");
        String fileName = file.getName();
        BlobPlugin plugin = blobFileManager.getPlugin();
        blobFileManager.addDirectory(objectDirectoryFilename, NamingConventions.toCamelCase(objectName));
        if (plugin.getResource(fileName) != null) {
            blobFileManager.addFile(objectBuilderFilename, file);
            if (blobFileManager.updateYAML(file))
                InventoryManager.continueLoadingBlobInventories(plugin, file);
        }
        return new ObjectDirectorData(objectDirectoryFilename, objectBuilderFilename, objectName);
    }

    /**
     * Constructs a new ObjectDirectorData.
     *
     * @param blobFileManager The BlobFileManager
     * @param objectName      The name of the object
     * @return The ObjectDirectorData
     */
    public static ObjectDirectorData simple(BlobFileManager blobFileManager,
                                            String objectName) {
        return registerAndBuild(blobFileManager, objectName + "Builder", objectName + "Directory", objectName);
    }
}
