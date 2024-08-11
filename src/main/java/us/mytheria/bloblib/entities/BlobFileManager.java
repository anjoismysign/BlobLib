package us.mytheria.bloblib.entities;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.bloblib.managers.Manager;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

/**
 * @author anjoismysign
 * A BlobFileManager will manage all files related to inventories,
 * messages, sounds and future objects that BlobLib will support.
 */
public class BlobFileManager extends Manager implements IFileManager {
    private final File pluginDirectory;
    private final Map<String, File> files = new HashMap<>();
    private final Map<DataAssetType, String> directories = new HashMap<>();
    private final String lowercased;

    /**
     * Will make a BlobFileManager from a ManagerDirector.
     * Doesn't work with GenericManagerDirector
     * since their #getPlugin() only works after
     * ManagerDirector constructor.
     *
     * @param managerDirector the manager director
     * @return the BlobFileManager
     */
    public static BlobFileManager of(ManagerDirector managerDirector) {
        BlobPlugin plugin = managerDirector.getPlugin();
        return new BlobFileManager(managerDirector, "plugins/" +
                plugin.getDataFolder().getPath(), plugin);
    }

    /**
     * creates a new BlobFileManager
     *
     * @param managerDirector         the manager director
     * @param pluginDirectoryPathname the path to the plugin directory
     */
    public BlobFileManager(ManagerDirector managerDirector,
                           String pluginDirectoryPathname,
                           JavaPlugin plugin) {
        super(managerDirector);
        this.lowercased = plugin.getName().toLowerCase();
        this.pluginDirectory = new File(pluginDirectoryPathname);
        for (DataAssetType assetType : DataAssetType.values()) {
            String key = assetType.getKey();
            directories.put(assetType, key);
            addFile(key, new File(pluginDirectory.getPath() + assetType.getDirectoryPath()));
            addFile(assetType.getDefaultFileKey(), new File(getDirectory(assetType).getPath() + "/" + lowercased + assetType.getDefaultFilePath()));
        }
        loadFiles(plugin);
    }

    /**
     * adds a directory to the files HashMap (which is stored in the RAM to
     * later be retrieved in a 0(1) time complexity)
     *
     * @param key        the key to store the file under
     * @param folderName the name of the folder to create
     * @return the file created
     */
    public File addDirectory(String key, String folderName) {
        File directory = new File(pluginDirectory.getPath() + "/" + folderName);
        addFile(key, directory);
        return directory;
    }

    /**
     * adds directories to the files HashMap (which is stored in the
     * RAM to later be retrieved in a 0(1) time complexity)
     *
     * @param map the map to add
     * @return the list of files added
     */
    public List<File> addDirectories(Map<String, String> map) {
        List<File> files = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            files.add(addDirectory(entry.getKey(), entry.getValue()));
        }
        return files;
    }

    /**
     * adds a file to the files HashMap (which is stored in the RAM to later
     * be retrieved in a 0(1) time complexity)
     *
     * @param key  the key to store the file under
     * @param file the file to store
     */
    public void addFile(String key, File file) {
        files.put(key, file);
    }

    /**
     * It will auto update it with the most recent
     * version that's embedded in the plugin jar.
     *
     * @param file the YAML file to update
     * @return if it's a fresh file / was just created.
     */
    public boolean updateYAML(File file) {
        return updateYAML(file, null);
    }

    /**
     * It will auto update it with the most recent
     * version that's embedded in the plugin jar.
     *
     * @param file the YAML file to update
     * @param path the path to the embedded file
     * @return if it's a fresh file / was just created.
     */
    public boolean updateYAML(File file, String path) {
        if (path == null)
            path = file.getName();
        String fileName = FilenameUtils.removeExtension(file.getName());
        file.getParentFile().mkdirs();
        try {
            boolean isFresh = file.createNewFile();
            ResourceUtil.updateYml(file.getParentFile(),
                    "/temp" + fileName + ".yml",
                    path, file, getPlugin());
            return isFresh;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates a YML file array with the most recent
     * version that's embedded in the plugin jar.
     *
     * @param files the files to update.
     */
    public void updateYAMLs(File... files) {
        for (File file : files) {
            updateYAML(file);
        }
    }

    /**
     * Creates and updates a YML file collection.
     *
     * @param files the files to create and update.
     */
    public void updateYAMLs(Collection<File> files) {
        for (File file : files) {
            updateYAML(file);
        }
    }

    /**
     * Retrieves a file from random access memory (RAM) which is faster
     * than reading from the hard drive.
     *
     * @param key the key of the file.
     * @return the filem, null if not found
     */
    @Nullable
    private File getFile(String key) {
        return files.get(key);
    }

    /**
     * Attempts to retrieve a file from random access memory (RAM) which is
     * faster than reading from the hard drive.
     *
     * @param key the key of the file.
     * @return the filen, an empty optional if the file is not found
     */
    public Optional<File> searchFile(String key) {
        return Optional.ofNullable(files.get(key));
    }
    
    private void loadFiles(JavaPlugin plugin) {
        try {
            for (DataAssetType assetType : DataAssetType.values()) {
                @Nullable File directory = getDirectory(assetType);
                Objects.requireNonNull(directory, "No directory for DataAssetType: " + assetType.name());
                if (!directory.exists())
                    directory.mkdir();
                String path = lowercased + assetType.getDefaultFilePath();
                Optional<InputStream> optional = Optional.ofNullable(plugin.getResource(path));
                if (optional.isPresent()) {
                    @Nullable File file = getFile(assetType.getDefaultFileKey());
                    Objects.requireNonNull(file, "No default file for DataAssetType: " + assetType.name());
                    file.createNewFile();
                    ResourceUtil.updateYml(getDirectory(assetType), "/temp" + path, path, file, plugin);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        File directory = new File(pluginDirectory.getPath() + path);
        try {
            Files.createDirectories(directory.toPath());
            File file = new File(directory + "/" + fileName + ".yml");
            Optional<InputStream> optional = Optional.ofNullable(getPlugin().getResource(fileName + ".yml"));
            if (optional.isPresent()) {
                try {
                    if (softUpdate && file.exists())
                        return;
                    file.createNewFile();
                    ResourceUtil.updateYml(directory, "/temp" + fileName + ".yml",
                            fileName + ".yml", file, getPlugin());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unpacks an embedded file from the plugin's jar's resources folder.
     * It will only generate the file if it doesn't
     * already exist, like if server admin removed it.
     *
     * @param path     the path to the file
     * @param fileName the name of the file
     */
    public void unpackYamlFile(String path, String fileName) {
        unpackYamlFile(path, fileName, true);
    }

    /**
     * Returns the plugin directory.
     *
     * @return the plugin directory.
     */
    @NotNull
    public File getPluginDirectory() {
        return pluginDirectory;
    }

    /**
     * Returns the YamlConfiguration of the file.
     *
     * @param f the file.
     * @return the YamlConfiguration of the file.
     */
    public YamlConfiguration getYml(File f) {
        return YamlConfiguration.loadConfiguration(f);
    }

    @NotNull
    public File getDirectory(DataAssetType type) {
        @Nullable File directory = getFile(directories.get(type));
        Objects.requireNonNull(directory, "'directory' is null");
        return directory;
    }
}
