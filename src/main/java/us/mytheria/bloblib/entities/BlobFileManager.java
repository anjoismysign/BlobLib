package us.mytheria.bloblib.entities;

import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.entities.manager.Manager;
import us.mytheria.bloblib.entities.manager.ManagerDirector;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;

public class BlobFileManager extends Manager {
    private final File path;
    private final HashMap<String, File> files;

    public BlobFileManager(ManagerDirector managerDirector, String pathname) {
        super(managerDirector);
        this.files = new HashMap<>();
        this.path = new File(pathname);
        addFile("messages", new File(path.getPath() + "/BlobMessage"));
        addFile("sounds", new File(path.getPath() + "/BlobSound"));
        addFile("inventories", new File(path.getPath() + "/Inventories"));
        String lowercased = getPlugin().getName().toLowerCase();
        addFile("defaultSounds", new File(soundsFolder().getPath() + "/" + lowercased + "_sounds.yml"));
        addFile("defaultMessages", new File(messagesFolder().getPath() + "/" + lowercased + "_lang.yml"));
        loadFiles();
    }

    public void addFile(String key, File file) {
        files.put(key, file);
    }

    private File getFile(String key) {
        return files.get(key);
    }

    public Optional<File> searchFile(String key) {
        return Optional.ofNullable(files.get(key));
    }

    public void loadFiles() {
        String lowercased = getPlugin().getName().toLowerCase();
        try {
            if (!path.exists()) path.mkdir();
            if (!messagesFolder().exists()) messagesFolder().mkdir();
            if (!soundsFolder().exists()) soundsFolder().mkdir();
            ///////////////////////////////////////////
            if (!getDefaultSounds().exists()) getDefaultSounds().createNewFile();
            if (!getDefaultMessages().exists()) getDefaultMessages().createNewFile();
            ResourceUtil.updateYml(soundsFolder(), "/temp" + lowercased + "_sounds.yml", lowercased + "_sounds.yml", getDefaultSounds(), getPlugin());
            ResourceUtil.updateYml(messagesFolder(), "/temp" + lowercased + "_lang.yml", lowercased + "_lang.yml", getDefaultMessages(), getPlugin());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getYml(File f) {
        return YamlConfiguration.loadConfiguration(f);
    }

    public File messagesFolder() {
        return getFile("messages");
    }

    public File soundsFolder() {
        return getFile("sounds");
    }

    public File inventoriesFolder() {
        return getFile("inventories");
    }

    public File getDefaultMessages() {
        return getFile("defaultMessages");
    }

    public File getDefaultSounds() {
        return getFile("defaultSounds");
    }
}
