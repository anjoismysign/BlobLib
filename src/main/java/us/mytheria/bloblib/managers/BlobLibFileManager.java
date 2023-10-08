package us.mytheria.bloblib.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

/**
 * @author anjoismysign
 */
public class BlobLibFileManager {
    private final BlobLib plugin;
    private final File path = new File("plugins/BlobLib");
    private final File messages = new File(path.getPath() + "/BlobMessage");
    private final File sounds = new File(path.getPath() + "/BlobSound");
    private final File inventories = new File(path.getPath() + "/BlobInventory");
    private final File metaInventories = new File(path.getPath() + "/MetaBlobInventory");
    private final File actions = new File(path.getPath() + "/Action");
    private final File snippets = new File(path.getPath() + "/TranslatableSnippet");
    private final File blocks = new File(path.getPath() + "/TranslatableBlock");
    private final File defaultSounds = new File(sounds.getPath() + "/bloblib_sounds.yml");
    private final File defaultMessages = new File(messages.getPath() + "/bloblib_lang.yml");
    private final File defaultInventories = new File(inventories.getPath() + "/bloblib_inventories.yml");
    private final File defaultMetaInventories = new File(metaInventories.getPath() + "/bloblib_meta_inventories.yml");
    private final File defaultActions = new File(actions.getPath() + "/bloblib_actions.yml");
    private final File defaultSnippets = new File(snippets.getPath() + "/bloblib_translatable_snippets.yml");
    private final File defaultBlocks = new File(blocks.getPath() + "/bloblib_translatable_blocks.yml");

    /**
     * Will create a new BlobLibFileManager instance
     */
    public BlobLibFileManager() {
        this.plugin = BlobLib.getInstance();
        loadFiles();
    }

    /**
     * Will load all files
     */
    public void loadFiles() {
        try {
            if (!path.exists()) path.mkdir();
            if (!messages.exists()) messages.mkdir();
            if (!sounds.exists()) sounds.mkdir();
            if (!inventories.exists()) inventories.mkdir();
            if (!metaInventories.exists()) metaInventories.mkdir();
            if (!actions.exists()) actions.mkdir();
            if (!snippets.exists()) snippets.mkdir();
            if (!blocks.exists()) blocks.mkdir();
            ///////////////////////////////////////////
            if (!defaultSounds.exists()) defaultSounds.createNewFile();
            if (!defaultMessages.exists()) defaultMessages.createNewFile();
            if (!defaultInventories.exists()) defaultInventories.createNewFile();
            if (!defaultMetaInventories.exists()) defaultMetaInventories.createNewFile();
            if (!defaultActions.exists()) defaultActions.createNewFile();
            if (!defaultSnippets.exists()) defaultSnippets.createNewFile();
            if (!defaultBlocks.exists()) defaultBlocks.createNewFile();
            ResourceUtil.updateYml(sounds, "/tempbloblib_sounds.yml", "bloblib_sounds.yml", defaultSounds, plugin);
            ResourceUtil.updateYml(messages, "/tempbloblib_lang.yml", "bloblib_lang.yml", defaultMessages, plugin);
            ResourceUtil.updateYml(inventories, "/tempInventories.yml", "bloblib_inventories.yml", defaultInventories, plugin);
            ResourceUtil.updateYml(metaInventories, "/tempMetaInventories.yml", "bloblib_meta_inventories.yml", defaultMetaInventories, plugin);
            ResourceUtil.updateYml(actions, "/tempActions.yml", "bloblib_actions.yml", defaultActions, plugin);
            ResourceUtil.updateYml(snippets, "/tempTranslatableSnippets.yml", "bloblib_translatable_snippets.yml", defaultSnippets, plugin);
            ResourceUtil.updateYml(blocks, "/tempTranslatableBlocks.yml", "bloblib_translatable_blocks.yml", defaultBlocks, plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Will load a YamlConfiguration from a file
     *
     * @param f The file to load
     * @return The YamlConfiguration
     */
    public YamlConfiguration getYml(File f) {
        return YamlConfiguration.loadConfiguration(f);
    }

    /**
     * Will return messages directory (INSIDE BlobLib PLUGIN DIRECTORY)
     *
     * @return The messages directory
     * @deprecated Use {@link #messagesDirectory()} instead to avoid confusion
     */
    @Deprecated
    public File messagesFile() {
        return messages;
    }

    /**
     * Will return messages directory (INSIDE BlobLib PLUGIN DIRECTORY)
     *
     * @return The messages directory
     */
    public File messagesDirectory() {
        return messages;
    }

    /**
     * Will return actions directory (INSIDE BlobLib PLUGIN DIRECTORY)
     *
     * @return The actions directory
     */
    public File actionsDirectory() {
        return actions;
    }

    /**
     * Will return sounds directory (INSIDE BlobLib PLUGIN DIRECTORY)
     *
     * @return The sounds directory
     * @deprecated Use {@link #soundsDirectory()} instead to avoid confusion
     */
    @Deprecated
    public File soundsFile() {
        return sounds;
    }

    /**
     * Will return sounds directory (INSIDE BlobLib PLUGIN DIRECTORY)
     *
     * @return The sounds directory
     */
    public File soundsDirectory() {
        return sounds;
    }


    /**
     * Will return inventories directory (INSIDE BlobLib PLUGIN DIRECTORY)
     *
     * @return The inventories directory
     * @deprecated Use {@link #inventoriesDirectory()} instead to avoid confusion
     */
    @Deprecated
    public File inventoriesFile() {
        return inventories;
    }

    /**
     * Will return inventories directory (INSIDE BlobLib PLUGIN DIRECTORY)
     *
     * @return The inventories directory
     */
    public File inventoriesDirectory() {
        return inventories;
    }

    /**
     * Will return meta inventories' directory (INSIDE BlobLib PLUGIN DIRECTORY)
     *
     * @return The meta inventories directory
     */
    public File metaInventoriesDirectory() {
        return metaInventories;
    }

    /**
     * Will return the default inventories file
     *
     * @return The default inventories file
     */
    public File defaultInventoriesFile() {
        return defaultInventories;
    }

    /**
     * Will return the default meta inventories file
     *
     * @return The default meta inventories file
     */
    public File defaultMetaInventoriesFile() {
        return defaultMetaInventories;
    }

    /**
     * Will return the default TranslatableSnippet directory
     *
     * @return The default TranslatableSnippets file
     */
    public File snippetsDirectory() {
        return snippets;
    }

    /**
     * Will return the default TranslatableBlock directory
     *
     * @return The default TranslatableBlocks file
     */
    public File blocksDirectory() {
        return blocks;
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
            File file = new File(directory + "/" + fileName + ".yml");
            Optional<InputStream> optional = Optional.ofNullable(plugin.getResource(fileName + ".yml"));
            if (optional.isPresent()) {
                try {
                    if (softUpdate && file.exists())
                        return;
                    file.createNewFile();
                    ResourceUtil.updateYml(directory, "/temp" + fileName + ".yml",
                            fileName + ".yml", file, plugin);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}