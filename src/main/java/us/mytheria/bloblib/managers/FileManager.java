package us.mytheria.bloblib.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;

public class FileManager {
    private BlobLib main;
    private File path = new File("plugins/BlobLib");
    private File lang = new File(path.getPath() + "/lang.yml");

    public FileManager() {
        this.main = BlobLib.getInstance();
        loadFiles();
    }

    public void loadFiles() {
        try {
            if (!path.exists()) path.mkdir();
            ///////////////////////////////////////////
            if (!lang.exists()) lang.createNewFile();
            ResourceUtil.updateYml(path, "/tempLang.yml", "lang.yml", lang, main);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getYml(File f) {
        return YamlConfiguration.loadConfiguration(f);
    }

    public File getLang() {
        return lang;
    }
}