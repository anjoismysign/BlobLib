package us.mytheria.bloblib.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;

public class FileManager {
    private final BlobLib main;
    private final File path = new File("plugins/BlobLib");
    private final File messages = new File(path.getPath() + "/messages");
    private final File lang = new File(messages.getPath() + "/bloblib_lang.yml");
    private final File inventories = new File(path.getPath() + "/inventories.yml");

    public FileManager() {
        this.main = BlobLib.getInstance();
        loadFiles();
    }

    public void loadFiles() {
        try {
            if (!path.exists()) path.mkdir();
            if (!messages.exists()) messages.mkdir();
            ///////////////////////////////////////////
            if (!lang.exists()) lang.createNewFile();
            if (!inventories.exists()) inventories.createNewFile();
            ResourceUtil.updateYml(path, "/tempLang.yml", "bloblib_lang.yml", lang, main);
            ResourceUtil.updateYml(path, "/tempInventories.yml", "inventories.yml", inventories, main);
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

    public File inventoriesFile() {
        return inventories;
    }
}