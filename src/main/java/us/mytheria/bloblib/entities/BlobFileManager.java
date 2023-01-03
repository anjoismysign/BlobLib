package us.mytheria.bloblib.entities;

import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.entities.manager.Manager;
import us.mytheria.bloblib.entities.manager.ManagerDirector;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;

public class BlobFileManager extends Manager {
    private final File path;
    private final File messages;
    private final File sounds;
    private final File defaultSounds;
    private final File defaultMessages;
    private final File inventories;

    public BlobFileManager(ManagerDirector managerDirector, String pathname) {
        super(managerDirector);
        this.path = new File(pathname);
        this.messages = new File(path.getPath() + "/BlobMessage");
        this.sounds = new File(path.getPath() + "/BlobSound");
        this.inventories = new File(path.getPath() + "/Inventories");
        String lowercased = getPlugin().getName().toLowerCase();
        this.defaultSounds = new File(sounds.getPath() + "/" + lowercased + "_sounds.yml");
        this.defaultMessages = new File(messages.getPath() + "/" + lowercased + "_lang.yml");
        loadFiles();
    }

    public void loadFiles() {
        String lowercased = getPlugin().getName().toLowerCase();
        try {
            if (!path.exists()) path.mkdir();
            if (!messages.exists()) messages.mkdir();
            if (!sounds.exists()) sounds.mkdir();
            ///////////////////////////////////////////
            if (!defaultSounds.exists()) defaultSounds.createNewFile();
            if (!defaultMessages.exists()) defaultMessages.createNewFile();
            if (!inventories.exists()) inventories.createNewFile();
            ResourceUtil.updateYml(sounds, "/temp" + lowercased + "_sounds.yml", lowercased + "_sounds.yml", defaultSounds, getPlugin());
            ResourceUtil.updateYml(messages, "/temp" + lowercased + "_lang.yml", lowercased + "_lang.yml", defaultMessages, getPlugin());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getYml(File f) {
        return YamlConfiguration.loadConfiguration(f);
    }

    public File messagesFolder() {
        return messages;
    }

    public File soundsFolder() {
        return sounds;
    }

    public File inventoriesFolder() {
        return inventories;
    }

    public File getDefaultMessages() {
        return defaultMessages;
    }

    public File getDefaultSounds() {
        return defaultSounds;
    }
}
