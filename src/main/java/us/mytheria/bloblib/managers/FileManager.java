package us.mytheria.bloblib.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;

public class FileManager {
    private BlobLib main;
    private final File path = new File("plugins/BlobLib");
    private final File lang = new File(path.getPath() + "/messages");
    private final File inventories = new File(path.getPath() + "/inventories.yml");

    public FileManager() {
        this.main = BlobLib.getInstance();
        loadFiles();
    }

    public void loadFiles() {
        try {
            if (!path.exists()) path.mkdir();
            if (!lang.exists()) lang.mkdir();
            ///////////////////////////////////////////
            if (!inventories.exists()) inventories.createNewFile();
            ResourceUtil.updateYml(path, "/tempLang.yml", "lang.yml", lang, main);
            ResourceUtil.updateYml(path, "/tempInventories.yml", "inventories.yml", inventories, main);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getYml(File f) {
        return YamlConfiguration.loadConfiguration(f);
    }

    public File langFile() {
        return lang;
    }

    public File inventoriesFile() {
        return inventories;
    }
}