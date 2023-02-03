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
import java.util.HashSet;
import java.util.Set;

public class MessageManager {
    private final BlobLib main;
    private HashMap<String, SerialBlobMessage> messages;
    private HashMap<BlobPlugin, Set<String>> pluginMessages;
    private HashMap<String, Integer> duplicates;

    public MessageManager() {
        this.main = BlobLib.getInstance();
    }

    public void reload() {
        load();
    }

    public void load() {
        messages = new HashMap<>();
        pluginMessages = new HashMap<>();
        duplicates = new HashMap<>();
        loadFiles(main.getFileManager().messagesDirectory());
        duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate BlobMessage: '" + key + "' (found " + value + " instances)"));
    }

    public void load(BlobPlugin plugin) {
        if (pluginMessages.containsKey(plugin))
            throw new IllegalArgumentException("Plugin '" + plugin.getName() + "' has already been loaded");
        pluginMessages.put(plugin, new HashSet<>());
        duplicates.clear();
        File directory = plugin.getManagerDirector().getFileManager().messagesDirectory();
        loadFiles(plugin, directory);
        duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                .log("Duplicate BlobMessage: '" + key + "' (found " + value + " instances)"));
    }

    public void unload(BlobPlugin plugin) {
        if (!pluginMessages.containsKey(plugin))
            throw new IllegalArgumentException("Plugin '" + plugin.getName() + "' has not been loaded");
        pluginMessages.get(plugin).forEach(messages::remove);
        pluginMessages.remove(plugin);
    }

    public static void unloadBlobPlugin(BlobPlugin plugin) {
        BlobLib.getInstance().getMessageManager().unload(plugin);
    }

    public static void loadBlobPlugin(BlobPlugin plugin) {
        MessageManager manager = BlobLib.getInstance().getMessageManager();
        manager.load(plugin);
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

    private void loadFiles(BlobPlugin plugin, File path) {
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
                if (messages.containsKey(mapKey)) {
                    addDuplicate(mapKey);
                    return;
                }
                messages.put(key + "." + subKey, BlobMessageReader.read(subSection));
            });
        });
    }

    private void loadYamlConfiguration(File file, BlobPlugin plugin) {
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
                if (messages.containsKey(mapKey)) {
                    addDuplicate(mapKey);
                    return;
                }
                messages.put(key + "." + subKey, BlobMessageReader.read(subSection));
                pluginMessages.get(plugin).add(key + "." + subKey);
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
        messages.get("System.No-Permission").sendAndPlay(player);
    }

    @Nullable
    public ReferenceBlobMessage getMessage(String key) {
        return new ReferenceBlobMessage(messages.get(key), key);
    }

    public void playAndSend(Player player, String key) {
        SerialBlobMessage message = messages.get(key);
        if (message == null)
            throw new NullPointerException("Message '" + key + "' does not exist!");
        message.sendAndPlay(player);
    }

    public void send(Player player, String key) {
        SerialBlobMessage message = messages.get(key);
        if (message == null)
            throw new NullPointerException("Message '" + key + "' does not exist!");
        message.send(player);
    }
}
