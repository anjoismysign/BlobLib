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
import java.util.*;

public class MessageManager {
    private final BlobLib main;
    private HashMap<String, SerialBlobMessage> messages;
    private HashMap<String, Set<String>> pluginMessages;
    private HashMap<String, Integer> duplicates;
    private HashMap<String, Map<String, SerialBlobMessage>> locales;

    public MessageManager() {
        this.main = BlobLib.getInstance();
    }

    public void reload() {
        load();
    }

    public void load() {
        messages = new HashMap<>();
        locales = new HashMap<>();
        pluginMessages = new HashMap<>();
        duplicates = new HashMap<>();
        loadFiles(main.getFileManager().messagesDirectory());
        duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate BlobMessage: '" + key + "' (found " + value + " instances)"));
    }

    public void load(BlobPlugin plugin, IManagerDirector director) {
        String pluginName = plugin.getName();
        if (pluginMessages.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has already been loaded");
        pluginMessages.put(pluginName, new HashSet<>());
        duplicates.clear();
        File directory = director.getFileManager().messagesDirectory();
        loadFiles(plugin, directory);
        duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                .log("Duplicate BlobMessage: '" + key + "' (found " + value + " instances)"));
    }

    public void unload(BlobPlugin plugin) {
        String pluginName = plugin.getName();
        Set<String> messages = this.pluginMessages.get(pluginName);
        if (messages == null)
            return;
        Iterator<String> iterator = messages.iterator();
        while (iterator.hasNext()) {
            String inventoryName = iterator.next();
            this.messages.remove(inventoryName);
            iterator.remove();
        }
        pluginMessages.remove(pluginName);
    }

    public static void unloadBlobPlugin(BlobPlugin plugin) {
        BlobLib.getInstance().getMessageManager().unload(plugin);
    }

    public static void loadBlobPlugin(BlobPlugin plugin, IManagerDirector director) {
        MessageManager manager = BlobLib.getInstance().getMessageManager();
        manager.load(plugin, director);
    }

    public static void loadBlobPlugin(BlobPlugin plugin) {
        loadBlobPlugin(plugin, plugin.getManagerDirector());
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
                loadFiles(file);
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
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Type") && !section.isString("Type"))
                return;
            if (messages.containsKey(reference)) {
                addDuplicate(reference);
                return;
            }
            SerialBlobMessage message = BlobMessageReader.read(section);
            messages.put(reference, message);
            addOrCreateLocale(message, reference);
        });
    }

    private void loadYamlConfiguration(File file, BlobPlugin plugin) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Type") && !section.isString("Type"))
                return;
            if (messages.containsKey(reference)) {
                addDuplicate(reference);
                return;
            }
            messages.put(reference, BlobMessageReader.read(section));
            pluginMessages.get(plugin.getName()).add(reference);
        });
    }

    private void addOrCreateLocale(SerialBlobMessage message, String reference) {
        String locale = message.getLocale();
        locales.computeIfAbsent(locale, k -> new HashMap<>()).put(reference, message);
    }

    /**
     * Loads a YamlConfiguration from a file and registers it to the plugin.
     * Will return false if the plugin has not been loaded.
     * Will print duplicates.
     * NOTE: Printing duplicates runs for each time you call this method!
     *
     * @param file   The file to load
     * @param plugin The plugin to register the messages to
     * @return Whether the plugin has been loaded
     */
    public static boolean loadAndRegisterYamlConfiguration(File file, BlobPlugin plugin) {
        MessageManager manager = BlobLib.getInstance().getMessageManager();
        if (!manager.pluginMessages.containsKey(plugin.getName()))
            return false;
        manager.loadYamlConfiguration(file, plugin);
        manager.duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate BlobMessage: '" + key + "' (found " + value + " instances)"));
        return true;
    }

    private void addDuplicate(String key) {
        if (duplicates.containsKey(key))
            duplicates.put(key, duplicates.get(key) + 1);
        else
            duplicates.put(key, 2);
    }

    public void noPermission(Player player) {
        messages.get("System.No-Permission").handle(player);
    }

    @Nullable
    public ReferenceBlobMessage getMessage(String key) {
        return new ReferenceBlobMessage(messages.get(key), key);
    }

    @Nullable
    public ReferenceBlobMessage getMessage(String key, String locale) {
        Map<String, SerialBlobMessage> localeMap = locales.get(locale);
        if (localeMap == null)
            return null;
        return new ReferenceBlobMessage(localeMap.get(key), key);
    }

    public void playAndSend(Player player, String key) {
        SerialBlobMessage message = messages.get(key);
        if (message == null)
            throw new NullPointerException("Message '" + key + "' does not exist!");
        message.handle(player);
    }

    public void send(Player player, String key) {
        SerialBlobMessage message = messages.get(key);
        if (message == null)
            throw new NullPointerException("Message '" + key + "' does not exist!");
        message.send(player);
    }
}
