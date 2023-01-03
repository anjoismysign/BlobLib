package us.mytheria.bloblib.entities;

import java.io.File;

public record ObjectDirectorData(String objectDirectory, String objectBuilderKey) {

    public static ObjectDirectorData registerAndBuild(BlobFileManager fileManager,
                                                      String objectBuilderFilename,
                                                      String objectDirectoryFilename,
                                                      String objectName) {
        File shopArticleBuilder = new File(fileManager.getPluginDirectory() + "/" + objectBuilderFilename + ".yml");
        String objectDirectory = objectName + "Directory";
        fileManager.addDirectory(objectDirectory, "articles");
        String objectBuilderKey = objectName + "Builder";
        fileManager.addFile(objectBuilderKey, shopArticleBuilder);
        fileManager.createAndUpdateYML(shopArticleBuilder);
        return new ObjectDirectorData(objectDirectory, objectBuilderKey);
    }

    public static ObjectDirectorData simple(BlobFileManager blobFileManager,
                                            String objectName) {
        return registerAndBuild(blobFileManager, objectName + "Builder", objectName.toLowerCase() + "Directory", objectName);
    }
}
