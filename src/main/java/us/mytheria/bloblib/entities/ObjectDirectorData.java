package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.NamingConventions;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.logger.BlobPluginLogger;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.bloblib.managers.InventoryManager;

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

        File file = new File(blobFileManager.inventoriesDirectory() + "/" + objectBuilderFilename + ".yml");
        String fileName = file.getName();
        BlobPluginLogger logger = BlobLib.getAnjoLogger();
        BlobPlugin plugin = blobFileManager.getPlugin();
        blobFileManager.addDirectory(objectDirectoryFilename, NamingConventions.toCamelCase(objectName));
        if (plugin.getResource(fileName) != null) {
            blobFileManager.addFile(objectBuilderFilename, file);
            if (blobFileManager.updateYAML(file))
                InventoryManager.continueLoading(plugin, file);
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
