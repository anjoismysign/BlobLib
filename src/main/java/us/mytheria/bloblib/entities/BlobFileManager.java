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
    private final Map<String, File> files;
    private final Map<DataAssetType, String> directories;
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
        this.directories = new HashMap<>();
        this.lowercased = plugin.getName().toLowerCase();
        this.files = new HashMap<>();
        this.pluginDirectory = new File(pluginDirectoryPathname);
        directories.put(DataAssetType.BLOB_MESSAGE, "messages");
        directories.put(DataAssetType.BLOB_SOUND, "sounds");
        directories.put(DataAssetType.BLOB_INVENTORY, "blobInventories");
        directories.put(DataAssetType.META_BLOB_INVENTORY, "metaBlobInventories");
        directories.put(DataAssetType.ACTION, "actions");
        directories.put(DataAssetType.TRANSLATABLE_SNIPPET, "translatableSnippets");
        directories.put(DataAssetType.TRANSLATABLE_BLOCK, "translatableBlocks");
        directories.put(DataAssetType.TRANSLATABLE_ITEM, "translatableItems");
        directories.put(DataAssetType.TAG_SET, "tagSets");
        addFile("messages", new File(pluginDirectory.getPath() + "/BlobMessage"));
        addFile("sounds", new File(pluginDirectory.getPath() + "/BlobSound"));
        addFile("blobInventories", new File(pluginDirectory.getPath() + "/BlobInventory"));
        addFile("metaBlobInventories", new File(pluginDirectory.getPath() + "/MetaBlobInventory"));
        addFile("actions", new File(pluginDirectory.getPath() + "/Action"));
        addFile("tagSets", new File(pluginDirectory.getPath() + "/TagSet"));
        addFile("translatableSnippets", new File(pluginDirectory.getPath() + "/TranslatableSnippet"));
        addFile("translatableBlocks", new File(pluginDirectory.getPath() + "/TranslatableBlock"));
        addFile("translatableItems", new File(pluginDirectory.getPath() + "/TranslatableItem"));
        addFile("defaultSounds", new File(getDirectory(DataAssetType.BLOB_SOUND).getPath() + "/" + lowercased + "_sounds.yml"));
        addFile("defaultMessages", new File(getDirectory(DataAssetType.BLOB_MESSAGE).getPath() + "/" + lowercased + "_lang.yml"));
        addFile("defaultBlobInventories", new File(getDirectory(DataAssetType.BLOB_INVENTORY).getPath() + "/" + lowercased + "_inventories.yml"));
        addFile("defaultMetaBlobInventories", new File(getDirectory(DataAssetType.META_BLOB_INVENTORY).getPath() + "/" + lowercased + "_meta_inventories.yml"));
        addFile("defaultActions", new File(getDirectory(DataAssetType.ACTION).getPath() + "/" + lowercased + "_actions.yml"));
        addFile("defaultTagSets", new File(getDirectory(DataAssetType.TAG_SET).getPath() + "/" + lowercased + "_tag_sets.yml"));
        addFile("defaultTranslatableSnippets", new File(getDirectory(DataAssetType.TRANSLATABLE_SNIPPET).getPath() + "/" + lowercased + "_translatable_snippets.yml"));
        addFile("defaultTranslatableBlocks", new File(getDirectory(DataAssetType.TRANSLATABLE_BLOCK).getPath() + "/" + lowercased + "_translatable_blocks.yml"));
        addFile("defaultTranslatableItems", new File(getDirectory(DataAssetType.TRANSLATABLE_ITEM).getPath() + "/" + lowercased + "_translatable_items.yml"));
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

    /**
     * Loads all files
     */
    public void loadFiles(JavaPlugin plugin) {
        try {
            if (!pluginDirectory.exists()) pluginDirectory.mkdir();
            if (!messagesDirectory().exists()) messagesDirectory().mkdir();
            if (!soundsDirectory().exists()) soundsDirectory().mkdir();
            if (!inventoriesDirectory().exists()) inventoriesDirectory().mkdir();
            if (!metaInventoriesDirectory().exists()) metaInventoriesDirectory().mkdir();
            if (!actionsDirectory().exists()) actionsDirectory().mkdir();
            if (!translatableSnippetsDirectory().exists()) translatableSnippetsDirectory().mkdir();
            if (!translatableBlocksDirectory().exists()) translatableBlocksDirectory().mkdir();
            if (!getDirectory(DataAssetType.TRANSLATABLE_ITEM).exists())
                getDirectory(DataAssetType.TRANSLATABLE_ITEM).mkdir();
            if (!getDirectory(DataAssetType.TAG_SET).exists())
                getDirectory(DataAssetType.TAG_SET).mkdir();
            ///////////////////////////////////////////
            Optional<InputStream> soundsOptional = Optional.ofNullable(plugin.getResource(lowercased + "_sounds.yml"));
            if (soundsOptional.isPresent()) {
                getDefaultSounds().createNewFile();
                ResourceUtil.updateYml(soundsDirectory(), "/temp" + lowercased + "_sounds.yml", lowercased + "_sounds.yml", getDefaultSounds(), plugin);
            }
            Optional<InputStream> langOptional = Optional.ofNullable(plugin.getResource(lowercased + "_lang.yml"));
            if (langOptional.isPresent()) {
                getDefaultMessages().createNewFile();
                ResourceUtil.updateYml(messagesDirectory(), "/temp" + lowercased + "_lang.yml", lowercased + "_lang.yml", getDefaultMessages(), plugin);
            }
            Optional<InputStream> inventoriesOptional = Optional.ofNullable(plugin.getResource(lowercased + "_inventories.yml"));
            if (inventoriesOptional.isPresent()) {
                getDefaultInventories().createNewFile();
                ResourceUtil.updateYml(inventoriesDirectory(), "/temp" + lowercased + "_inventories.yml", lowercased + "_inventories.yml", getDefaultInventories(), plugin);
            }
            Optional<InputStream> metaInventoriesOptional = Optional.ofNullable(plugin.getResource(lowercased + "_meta_inventories.yml"));
            if (metaInventoriesOptional.isPresent()) {
                getDefaultMetaInventories().createNewFile();
                ResourceUtil.updateYml(metaInventoriesDirectory(), "/temp" + lowercased + "_meta_inventories.yml", lowercased + "_meta_inventories.yml", getDefaultMetaInventories(), plugin);
            }
            Optional<InputStream> actionsOptional = Optional.ofNullable(plugin.getResource(lowercased + "_actions.yml"));
            if (actionsOptional.isPresent()) {
                getDefaultActions().createNewFile();
                ResourceUtil.updateYml(actionsDirectory(), "/temp" + lowercased + "_actions.yml", lowercased + "_actions.yml", getDefaultActions(), plugin);
            }
            Optional<InputStream> translatableSnippetsOptional = Optional.ofNullable(plugin.getResource(lowercased + "_translatable_snippets.yml"));
            if (translatableSnippetsOptional.isPresent()) {
                getDefaultTranslatableSnippets().createNewFile();
                ResourceUtil.updateYml(translatableSnippetsDirectory(), "/temp" + lowercased + "_translatable_snippets.yml", lowercased + "_translatable_snippets.yml", getDefaultTranslatableSnippets(), plugin);
            }
            Optional<InputStream> translatableBlocksOptional = Optional.ofNullable(plugin.getResource(lowercased + "_translatable_blocks.yml"));
            if (translatableBlocksOptional.isPresent()) {
                getDefaultTranslatableBlocks().createNewFile();
                ResourceUtil.updateYml(translatableBlocksDirectory(), "/temp" + lowercased + "_translatable_blocks.yml", lowercased + "_translatable_blocks.yml", getDefaultTranslatableBlocks(), plugin);
            }
            Optional<InputStream> translatableItemsOptional = Optional.ofNullable(plugin.getResource(lowercased + "_translatable_items.yml"));
            if (translatableItemsOptional.isPresent()) {
                File file = getFile("defaultTranslatableItems");
                file.createNewFile();
                ResourceUtil.updateYml(getDirectory(DataAssetType.TRANSLATABLE_ITEM), "/temp" + lowercased + "_translatable_items.yml", lowercased + "_translatable_items.yml", file, plugin);
            }
            Optional<InputStream> tagSetsOptional = Optional.ofNullable(plugin.getResource(lowercased + "_tag_sets.yml"));
            if (tagSetsOptional.isPresent()) {
                File file = getFile("defaultTagSets");
                file.createNewFile();
                ResourceUtil.updateYml(getDirectory(DataAssetType.TAG_SET), "/temp" + lowercased + "_tag_sets.yml", lowercased + "_tag_sets.yml", file, plugin);
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

    @Nullable
    public File getDirectory(DataAssetType type) {
        return directories.get(type) == null ? null : getFile(directories.get(type));
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
        return getFile("blobInventories");
    }

    @NotNull
    public File metaInventoriesDirectory() {
        return getFile("metaBlobInventories");
    }

    @NotNull
    public File actionsDirectory() {
        return getFile("actions");
    }

    @NotNull
    public File translatableSnippetsDirectory() {
        return getFile("translatableSnippets");
    }

    @NotNull
    public File translatableBlocksDirectory() {
        return getFile("translatableBlocks");
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
        return getFile("defaultBlobInventories");
    }

    @NotNull
    public File getDefaultMetaInventories() {
        return getFile("defaultMetaInventories");
    }

    @NotNull
    public File getDefaultActions() {
        return getFile("defaultActions");
    }

    @NotNull
    public File getDefaultTranslatableSnippets() {
        return getFile("defaultTranslatableSnippets");
    }

    @NotNull
    public File getDefaultTranslatableBlocks() {
        return getFile("defaultTranslatableBlocks");
    }
}
