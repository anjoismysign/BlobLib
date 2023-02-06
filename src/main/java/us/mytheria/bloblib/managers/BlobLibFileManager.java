package us.mytheria.bloblib.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;

/**
 * @author anjoismysign
 */
public class BlobLibFileManager {
    private final BlobLib main;
    private final File path = new File("plugins/BlobLib");
    private final File messages = new File(path.getPath() + "/BlobMessage");
    private final File sounds = new File(path.getPath() + "/BlobSound");
    private final File inventories = new File(path.getPath() + "/BlobInventory");
    private final File defaultSounds = new File(sounds.getPath() + "/bloblib_sounds.yml");
    private final File defaultMessages = new File(messages.getPath() + "/bloblib_lang.yml");
    private final File defaultInventories = new File(inventories.getPath() + "/bloblib_inventories.yml");

    /**
     * Will create a new BlobLibFileManager instance
     */
    public BlobLibFileManager() {
        this.main = BlobLib.getInstance();
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
            ///////////////////////////////////////////
            if (!defaultSounds.exists()) defaultSounds.createNewFile();
            if (!defaultMessages.exists()) defaultMessages.createNewFile();
            if (!defaultInventories.exists()) defaultInventories.createNewFile();
            ResourceUtil.updateYml(sounds, "/tempbloblib_sounds.yml", "bloblib_sounds.yml", defaultSounds, main);
            ResourceUtil.updateYml(messages, "/tempbloblib_lang.yml", "bloblib_lang.yml", defaultMessages, main);
            ResourceUtil.updateYml(inventories, "/tempInventories.yml", "bloblib_inventories.yml", defaultInventories, main);
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
     * Will return the default inventories file
     *
     * @return The default inventories file
     */
    public File defaultInventoriesFile() {
        return defaultInventories;
    }
}