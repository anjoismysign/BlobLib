package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.entities.DataAssetType;
import io.github.anjoismysign.bloblib.entities.IFileManager;
import io.github.anjoismysign.bloblib.utilities.ResourceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author anjoismysign
 */
public class BlobLibFileManager implements IFileManager {
    private static final String LOWERCASED = "bloblib";

    private final BlobLib plugin;
    private final File path = new File("plugins" + File.separator + "BlobLib");
    private final Map<String, File> files = new HashMap<>();
    private final Map<DataAssetType, String> directories = new HashMap<>();

    /**
     * Will create a new BlobLibFileManager instance
     */
    public BlobLibFileManager() {
        this.plugin = BlobLib.getInstance();
        for (DataAssetType assetType : DataAssetType.values()) {
            String key = assetType.getKey();
            directories.put(assetType, key);
            addFile(key, new File(path.getPath() + assetType.getDirectoryPath()));
            addFile(assetType.getDefaultFileKey(), new File(getDirectory(assetType).getPath() + File.separator + LOWERCASED + assetType.getDefaultFilePath()));
        }
        loadFiles();
    }

    private void addFile(String key, File file) {
        files.put(key, file);
    }

    @Nullable
    private File getFile(String key) {
        return files.get(key);
    }

    /**
     * Will load all files
     */
    public void loadFiles() {
        try {
            for (DataAssetType assetType : DataAssetType.values()) {
                String path = LOWERCASED + assetType.getDefaultFilePath();
                Optional<InputStream> optional = Optional.ofNullable(plugin.getResource(path));
                if (optional.isPresent()) {
                    @Nullable File file = getFile(assetType.getDefaultFileKey());
                    Objects.requireNonNull(file, "No default file for DataAssetType: " + assetType.name());
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    ResourceUtil.updateYml(getDirectory(assetType), File.separator + "temp" + path, path, file, plugin);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Unpacks an embedded file from the plugin's jar's resources folder.
     * If softUpdate is true, it will only generate the file if it doesn't
     * already exist, like if server admin removed it.
     * If softUpdate is false, it will always try to attempt to update
     * the file with the most recent version embedded in the plugin jar.
     * Default sounds, messages, and inventories are 'hard' updated.
     * In case you would like to let the user modify the file and not
     * have it overwritten, you can use softUpdate!
     *
     * @param path       the path to the file
     * @param fileName   the name of the file
     * @param softUpdate if it should only update if the file doesn't exist
     */
    public void unpackYamlFile(String path, String fileName, boolean softUpdate) {
        File directory = new File(this.path + path);
        try {
            Files.createDirectories(directory.toPath());
            File file = new File(directory + File.separator + fileName + ".yml");
            Optional<InputStream> optional = Optional.ofNullable(plugin.getResource(fileName + ".yml"));
            if (optional.isPresent()) {
                try {
                    if (softUpdate && file.exists())
                        return;
                    file.createNewFile();
                    ResourceUtil.updateYml(directory, File.separator + "temp" + fileName + ".yml",
                            fileName + ".yml", file, plugin);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @NotNull
    public File getDirectory(DataAssetType type) {
        @Nullable File directory = getFile(directories.get(type));
        Objects.requireNonNull(directory, "'directory' is null");
        return directory;
    }
}