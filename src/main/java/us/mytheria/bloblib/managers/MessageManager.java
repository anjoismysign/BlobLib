package us.mytheria.bloblib.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobMessageReader;
import us.mytheria.bloblib.entities.message.ReferenceBlobMessage;
import us.mytheria.bloblib.entities.message.SerialBlobMessage;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;

public class MessageManager {
    private final BlobLib main;
    private HashMap<String, SerialBlobMessage> lang;
    private HashMap<String, Integer> duplicates;

    public MessageManager() {
        this.main = BlobLib.getInstance();
    }

    public void reload() {
        load();
    }

    public void load() {
        lang = new HashMap<>();
        duplicates = new HashMap<>();
        loadFiles(main.getFileManager().messagesFile());
        duplicates.forEach((key, value) -> main.getLogger()
                .severe("Duplicate BlobMessage: '" + key + "' (found " + value + " instances)"));
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
                if (!section.isConfigurationSection(subKey))
                    return;
                ConfigurationSection subSection = section.getConfigurationSection(subKey);
                if (!subSection.isString("Type"))
                    return;
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
            duplicates.put(key, 2);
    }

    public void noPermission(Player player) {
        lang.get("System.No-Permission").sendAndPlay(player);
    }

    @Nullable
    public ReferenceBlobMessage getMessage(String key) {
        return new ReferenceBlobMessage(lang.get(key), key);
    }

    public void playAndSend(Player player, String key) {
        SerialBlobMessage message = lang.get(key);
        if (message == null)
            throw new NullPointerException("Message '" + key + "' does not exist!");
        message.sendAndPlay(player);
    }

    public void send(Player player, String key) {
        SerialBlobMessage message = lang.get(key);
        if (message == null)
            throw new NullPointerException("Message '" + key + "' does not exist!");
        message.send(player);
    }
}
