package us.mytheria.bloblib.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;

public class FileManager {
    private final BlobLib main;
    private final File path = new File("plugins/BlobLib");
    private final File messages = new File(path.getPath() + "/BlobMessage");
    private final File sounds = new File(path.getPath() + "/BlobSound");
    private final File inventories = new File(path.getPath() + "/BlobInventory");
    private final File defaultSounds = new File(sounds.getPath() + "/bloblib_sounds.yml");
    private final File defaultMessages = new File(messages.getPath() + "/bloblib_lang.yml");
    private final File defaultInventories = new File(path.getPath() + "/bloblib_inventories.yml");

    public FileManager() {
        this.main = BlobLib.getInstance();
        loadFiles();
    }

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

    public YamlConfiguration getYml(File f) {
        return YamlConfiguration.loadConfiguration(f);
    }

    public File messagesFile() {
        return messages;
    }

    public File soundsFile() {
        return sounds;
    }

    public File defaultInventoriesFile() {
        return defaultInventories;
    }
}