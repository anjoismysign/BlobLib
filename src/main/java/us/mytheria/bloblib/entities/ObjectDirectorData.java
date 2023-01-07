package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.ConventionHelper;
import me.anjoismysign.anjo.entities.NamingConventions;

import java.io.File;

public record ObjectDirectorData(String objectDirectory, String objectBuilderKey, String objectName) {

    public static ObjectDirectorData registerAndBuild(BlobFileManager fileManager,
                                                      String objectBuilderFilename,
                                                      String objectDirectoryFilename,
                                                      String objectName) {
        File shopArticleBuilder = new File(fileManager.getPluginDirectory() + "/" + objectBuilderFilename + ".yml");
        String objectDirectory = objectName + "Directory";
        fileManager.addDirectory(objectDirectory, NamingConventions.toCamelCase(objectDirectoryFilename));
        String objectBuilderKey = objectName + "Builder";
        fileManager.addFile(objectBuilderKey, shopArticleBuilder);
        fileManager.createAndUpdateYML(shopArticleBuilder);
        return new ObjectDirectorData(objectDirectory, objectBuilderKey, objectName);
    }

    public static ObjectDirectorData simple(BlobFileManager blobFileManager,
                                            String objectName) {
        return registerAndBuild(blobFileManager, objectName + "Builder", objectName + "Directory", objectName);
    }

    public ConventionHelper objectNameConvention() {
        return ConventionHelper.from(objectName);
    }
}
