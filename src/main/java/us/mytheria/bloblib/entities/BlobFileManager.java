package us.mytheria.bloblib.entities;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.managers.Manager;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author anjoismysign
 * A BlobFileManager will manage all files related to inventories,
 * messages, sounds and future objects that BlobLib will support.
 */
public class BlobFileManager extends Manager {
    private final File pluginDirectory;
    private final HashMap<String, File> files;
    private final String lowercased = getPlugin().getName().toLowerCase();

    /**
     * creates a new BlobFileManager
     *
     * @param managerDirector         the manager director
     * @param pluginDirectoryPathname the path to the plugin directory
     */
    public BlobFileManager(ManagerDirector managerDirector,
                           String pluginDirectoryPathname) {
        super(managerDirector);
        this.files = new HashMap<>();
        this.pluginDirectory = new File(pluginDirectoryPathname);
        addFile("messages", new File(pluginDirectory.getPath() + "/BlobMessage"));
        addFile("sounds", new File(pluginDirectory.getPath() + "/BlobSound"));
        addFile("inventories", new File(pluginDirectory.getPath() + "/BlobInventory"));
        addFile("defaultSounds", new File(soundsDirectory().getPath() + "/" + lowercased + "_sounds.yml"));
        addFile("defaultMessages", new File(messagesDirectory().getPath() + "/" + lowercased + "_lang.yml"));
        addFile("defaultInventories", new File(inventoriesDirectory().getPath() + "/" + lowercased + "_inventories.yml"));
        loadFiles();
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
        String fileName = FilenameUtils.removeExtension(file.getName());
        try {
            boolean isFresh = file.createNewFile();
            if (isFresh)
                ResourceUtil.updateYml(file.getParentFile(),
                        "/temp" + fileName + ".yml",
                        fileName + ".yml", file, getPlugin());
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

    /**
     * Loads all files
     */
    public void loadFiles() {
        try {
            if (!pluginDirectory.exists()) pluginDirectory.mkdir();
            if (!messagesDirectory().exists()) messagesDirectory().mkdir();
            if (!soundsDirectory().exists()) soundsDirectory().mkdir();
            if (!inventoriesDirectory().exists()) inventoriesDirectory().mkdir();
            ///////////////////////////////////////////

            JavaPlugin main = getPlugin();
            Optional<InputStream> soundsOptional = Optional.ofNullable(main.getResource(lowercased + "_sounds.yml"));
            if (soundsOptional.isPresent()) {
                if (getDefaultSounds().createNewFile())
                    return;
                ResourceUtil.updateYml(soundsDirectory(), "/temp" + lowercased + "_sounds.yml", lowercased + "_sounds.yml", getDefaultSounds(), getPlugin());
            }
            Optional<InputStream> langOptional = Optional.ofNullable(main.getResource(lowercased + "_lang.yml"));
            if (langOptional.isPresent()) {
                if (getDefaultMessages().createNewFile())
                    return;
                ResourceUtil.updateYml(messagesDirectory(), "/temp" + lowercased + "_lang.yml", lowercased + "_lang.yml", getDefaultMessages(), getPlugin());
            }
            Optional<InputStream> inventoriesOptional = Optional.ofNullable(main.getResource(lowercased + "_inventories.yml"));
            if (inventoriesOptional.isPresent()) {
                if (getDefaultInventories().createNewFile())
                    return;
                ResourceUtil.updateYml(inventoriesDirectory(), "/temp" + lowercased + "_inventories.yml", lowercased + "_inventories.yml", getDefaultInventories(), getPlugin());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unpacks an embedded file from the plugin's jar's resources folder.
     * If updatable is true, it will attempt to update the file the same
     * way it updates the default messages, sounds, and inventories.
     * If false, it will only generate it one time in their
     * entire lifetime unless the file is deleted.
     *
     * @param path        the path to the file
     * @param fileName    the name of the file
     * @param isUpdatable if the file is updatable
     */
    public void unpackYamlFile(String path, String fileName, boolean isUpdatable) {
        File directory = new File(pluginDirectory.getPath() + path);
        File file = new File(directory + "/" + fileName + ".yml");
        Optional<InputStream> optional = Optional.ofNullable(getPlugin().getResource(fileName + ".yml"));
        if (optional.isPresent()) {
            try {
                if (file.createNewFile())
                    return;
                if (isUpdatable)
                    ResourceUtil.updateYml(directory, "/temp" + fileName + ".yml",
                            fileName + ".yml", file, getPlugin());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Unpacks an embedded file from the plugin's jar's resources folder.
     * Will only generate it one time in their
     * entire lifetime unless the file is deleted.
     *
     * @param path     the path to the file
     * @param fileName the name of the file
     */
    public void unpackYamlFile(String path, String fileName) {
        unpackYamlFile(path, fileName, false);
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

    /**
     * Returns the messages folder.
     *
     * @return the messages folder.
     */
    @NotNull
    public File messagesDirectory() {
        return getFile("messages");
    }

    /**
     * Returns the sounds' folder.
     *
     * @return the sounds' folder.
     */
    @NotNull
    public File soundsDirectory() {
        return getFile("sounds");
    }

    /**
     * Returns the inventories' folder.
     *
     * @return the inventories' folder.
     */
    @NotNull
    public File inventoriesDirectory() {
        return getFile("inventories");
    }

    /**
     * Returns the default messages file.
     *
     * @return the default messages file.
     */
    @NotNull
    public File getDefaultMessages() {
        return getFile("defaultMessages");
    }

    /**
     * Returns the default sounds file.
     *
     * @return the default sounds file.
     */
    @NotNull
    public File getDefaultSounds() {
        return getFile("defaultSounds");
    }

    /**
     * Returns the default inventories file.
     *
     * @return the default inventories file.
     */
    @NotNull
    public File getDefaultInventories() {
        return getFile("defaultInventories");
    }
}
