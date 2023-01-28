package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.ConventionHelper;
import me.anjoismysign.anjo.entities.NamingConventions;

import java.io.File;

public record ObjectDirectorData(String objectDirectory, String objectBuilderKey, String objectName) {

    public static ObjectDirectorData registerAndBuild(BlobFileManager fileManager,
                                                      String objectBuilderFilename,
                                                      String objectDirectoryFilename,
                                                      String objectName) {
        File file = new File(fileManager.getPluginDirectory() + "/" + objectBuilderFilename + ".yml");
        fileManager.addDirectory(objectDirectoryFilename, NamingConventions.toCamelCase(objectDirectoryFilename));
        fileManager.addFile(objectBuilderFilename, file);
        fileManager.registerAndUpdateYAML(file);
        return new ObjectDirectorData(objectDirectoryFilename, objectBuilderFilename, objectName);
    }

    public static ObjectDirectorData simple(BlobFileManager blobFileManager,
                                            String objectName) {
        return registerAndBuild(blobFileManager, objectName + "Builder", objectName + "Directory", objectName);
    }

    public ConventionHelper objectNameConvention() {
        return ConventionHelper.from(objectName);
    }
}
