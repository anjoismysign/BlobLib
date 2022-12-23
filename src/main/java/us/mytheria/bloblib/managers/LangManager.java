package us.mytheria.bloblib.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobMessageReader;
import us.mytheria.bloblib.entities.message.BlobMessage;

import java.io.File;
import java.util.HashMap;

public class LangManager {
    private final BlobLib main;
    private HashMap<String, BlobMessage> lang;
    private HashMap<String, Integer> duplicates;

    public LangManager() {
        this.main = BlobLib.getInstance();
        load();
    }

    public void reload() {
        load();
    }

    public void load() {
        lang = new HashMap<>();
        duplicates = new HashMap<>();
        loadFiles(main.getFileManager().messagesFile());
        duplicates.forEach((key, value) -> main.getLogger().severe("Duplicate key: '" + key + "' (" + value + " times)"));
    }

    private void loadFiles(File path) {
        File[] listOfFiles = path.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().equals(".DS_Store"))
                    continue;
                loadYamlConfiguration(file);
            }
            if (file.isDirectory())
                loadFiles(path);
        }
    }

    private void loadYamlConfiguration(File file) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.getKeys(false).forEach(key -> {
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(key);
            section.getKeys(true).forEach(subKey -> {
                if (section.isConfigurationSection(subKey))
                    return;
                ConfigurationSection subSection = section.getConfigurationSection(subKey);
                String mapKey = key + "." + subKey;
                if (lang.containsKey(mapKey)) {
                    addDuplicate(mapKey);
                    return;
                }
                lang.put(key + "." + subKey, BlobMessageReader.read(subSection));
            });
        });
    }

    private void addDuplicate(String key) {
        if (duplicates.containsKey(key))
            duplicates.put(key, duplicates.get(key) + 1);
        else
            duplicates.put(key, 1);
    }

    public void noPermission(Player player) {
        lang.get("Message.No-Permission").sendAndPlay(player);
    }

    public BlobMessage getLang(String key) {
        return lang.get(key);
    }

    public void playAndSend(Player player, String key) {
        BlobMessage message = lang.get(key);
        if (message == null)
            throw new NullPointerException("Message '" + key + "' does not exist!");
        message.sendAndPlay(player);
    }

    public void send(Player player, String key) {
        BlobMessage message = lang.get(key);
        if (message == null)
            throw new NullPointerException("Message '" + key + "' does not exist!");
        message.send(player);
    }
}
