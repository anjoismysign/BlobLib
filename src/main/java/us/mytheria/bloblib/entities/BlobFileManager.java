package us.mytheria.bloblib.entities;

import org.bukkit.configuration.file.YamlConfiguration;
import us.mytheria.bloblib.entities.manager.Manager;
import us.mytheria.bloblib.entities.manager.ManagerDirector;
import us.mytheria.bloblib.utilities.ResourceUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BlobFileManager extends Manager {
    private final File path;
    private final HashMap<String, File> files;
    private final String lowercased = getPlugin().getName().toLowerCase();

    public BlobFileManager(ManagerDirector managerDirector, String pathname) {
        super(managerDirector);
        this.files = new HashMap<>();
        this.path = new File(pathname);
        addFile("messages", new File(path.getPath() + "/BlobMessage"));
        addFile("sounds", new File(path.getPath() + "/BlobSound"));
        addFile("inventories", new File(path.getPath() + "/Inventories"));
        addFile("defaultSounds", new File(soundsFolder().getPath() + "/" + lowercased + "_sounds.yml"));
        addFile("defaultMessages", new File(messagesFolder().getPath() + "/" + lowercased + "_lang.yml"));
        loadFiles();
    }

    public File addDirectory(String key, String folderName) {
        File directory = new File(path.getPath() + "/" + folderName);
        addFile(key, directory);
        return directory;
    }

    public List<File> addDirectories(Map<String, String> map) {
        List<File> files = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            files.add(addDirectory(entry.getKey(), entry.getValue()));
        }
        return files;
    }

    public void addFile(String key, File file) {
        files.put(key, file);
    }

    public void createAndUpdateYML(File yamlFile) {
        String fileName = yamlFile.getName();
        try {
            boolean newFile = yamlFile.createNewFile();
            if (newFile)
                return;
            ResourceUtil.updateYml(yamlFile.getParentFile(),
                    "/temp" + fileName,
                    fileName, getDefaultMessages(), getPlugin());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createAndUpdateYMLs(File... files) {
        for (File file : files) {
            createAndUpdateYML(file);
        }
    }

    public void createAndUpdateYMLs(Collection<File> files) {
        for (File file : files) {
            createAndUpdateYML(file);
        }
    }

    private File getFile(String key) {
        return files.get(key);
    }

    public Optional<File> searchFile(String key) {
        return Optional.ofNullable(files.get(key));
    }

    public void loadFiles() {
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
