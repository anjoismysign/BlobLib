package us.mytheria.bloblib.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.entities.BlobMessageReader;
import us.mytheria.bloblib.entities.message.ReferenceBlobMessage;
import us.mytheria.bloblib.entities.message.SerialBlobMessage;

import java.io.File;
import java.util.*;

public class MessageManager {
    private final BlobLib main;
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
        loadFiles(directory);
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
            this.locales.forEach((locale, map) -> map.remove(inventoryName));
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
                try {
                    loadYamlConfiguration(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
            if (file.isDirectory())
                loadFiles(file);
        }
    }

    private void loadYamlConfiguration(File file) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String locale = yamlConfiguration.getString("Locale", "en_us");
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Type") && !section.isString("Type"))
                return;
            SerialBlobMessage message = BlobMessageReader.read(section, locale);
            addOrCreateLocale(message, reference);
        });
    }

    private void loadYamlConfiguration(File file, BlobPlugin plugin) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String locale = yamlConfiguration.getString("Locale", "en_us");
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Type") && !section.isString("Type"))
                return;
            if (!addOrCreateLocale(BlobMessageReader.read(section, locale), reference))
                return;
            pluginMessages.get(plugin.getName()).add(reference);
        });
    }

    private boolean addOrCreateLocale(SerialBlobMessage message, String reference) {
        String locale = message.getLocale();
        Map<String, SerialBlobMessage> localeMap = locales.computeIfAbsent(locale, k -> new HashMap<>());
        if (localeMap.containsKey(reference)) {
            addDuplicate(reference);
            return false;
        }
        localeMap.put(reference, message);
        return true;
    }

    public static void continueLoadingMessages(BlobPlugin plugin, boolean warnDuplicates, File... files) {
        MessageManager manager = BlobLib.getInstance().getMessageManager();
        manager.duplicates.clear();
        for (File file : files)
            manager.loadYamlConfiguration(file, plugin);
        if (warnDuplicates)
            manager.duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                    .log("Duplicate BlobMessage: '" + key + "' (found " + value + " instances)"));
    }

    private void addDuplicate(String key) {
        if (duplicates.containsKey(key))
            duplicates.put(key, duplicates.get(key) + 1);
        else
            duplicates.put(key, 2);
    }

    public void noPermission(Player player) {
        locales.get("en_us").get("System.No-Permission").handle(player);
    }

    @Nullable
    public ReferenceBlobMessage getMessage(String key) {
        return new ReferenceBlobMessage(locales.get("en_us").get(key), key);
    }

    @Nullable
    public ReferenceBlobMessage getMessage(String key, String locale) {
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        Map<String, SerialBlobMessage> localeMap = locales.get(locale);
        if (localeMap == null || !localeMap.containsKey(key))
            localeMap = locales.get("en_us");
        SerialBlobMessage message = localeMap.get(key);
        if (message == null)
            return null;
        return new ReferenceBlobMessage(message, key);
    }

    public void playAndSend(Player player, String key) {
        String locale = player.getLocale();
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        ReferenceBlobMessage message = getMessage(key, locale);
        if (message == null)
            throw new NullPointerException("Message '" + key + "' does not exist!");
        message.handle(player);
    }

    /**
     * @param player The player to send the message to
     * @param key    The key of the message
     * @deprecated Use {@link #playAndSend(Player, String)} instead
     */
    @Deprecated
    public void send(Player player, String key) {
        String locale = player.getLocale();
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        ReferenceBlobMessage message = getMessage(key, locale);
        if (message == null)
            throw new NullPointerException("Message '" + key + "' does not exist!");
        message.send(player);
    }
}
