package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.NamingConventions;

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
        blobFileManager.addDirectory(objectDirectoryFilename, NamingConventions.toCamelCase(objectDirectoryFilename));
        blobFileManager.addFile(objectBuilderFilename, file);
        blobFileManager.registerAndUpdateYAML(file);
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
