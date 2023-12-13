package us.mytheria.bloblib.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobSoundReader;
import us.mytheria.bloblib.entities.message.BlobSound;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SoundManager {
    private final BlobLib main;
    private HashMap<String, BlobSound> sounds;
    private HashMap<String, Set<String>> pluginSounds;
    private HashMap<String, Integer> duplicates;

    public SoundManager() {
        this.main = BlobLib.getInstance();
    }

    public void reload() {
        load();
    }

    public void load() {
        sounds = new HashMap<>();
        pluginSounds = new HashMap<>();
        duplicates = new HashMap<>();
        loadFiles(main.getFileManager().soundsDirectory());
        duplicates.forEach((key, value) -> main.getLogger()
                .severe("Duplicate BlobSound: '" + key + "' (found " + value + " instances)"));
    }

    public void load(BlobPlugin plugin, IManagerDirector director) {
        String pluginName = plugin.getName();
        if (pluginSounds.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has already been loaded");
        pluginSounds.put(pluginName, new HashSet<>());
        duplicates.clear();
        File directory = director.getFileManager().soundsDirectory();
        loadFiles(directory);
        duplicates.forEach((key, value) -> main.getLogger()
                .severe("Duplicate BlobSound: '" + key + "' (found " + value + " instances)"));
    }

    public static void loadBlobPlugin(BlobPlugin plugin, IManagerDirector director) {
        SoundManager manager = BlobLib.getInstance().getSoundManager();
        manager.load(plugin, director);
    }

    public void unload(BlobPlugin plugin) {
        String pluginName = plugin.getName();
        Set<String> sounds = this.pluginSounds.get(pluginName);
        if (sounds == null)
            return;
        Iterator<String> iterator = sounds.iterator();
        while (iterator.hasNext()) {
            String inventoryName = iterator.next();
            this.sounds.remove(inventoryName);
            iterator.remove();
        }
        pluginSounds.remove(pluginName);
    }

    public static void unloadBlobPlugin(BlobPlugin plugin) {
        SoundManager manager = BlobLib.getInstance().getSoundManager();
        manager.unload(plugin);
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

    private void loadYamlConfiguration(BlobPlugin plugin, File file) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Sound") && !section.isString("Sound"))
                return;
            if (sounds.containsKey(reference)) {
                addDuplicate(reference);
                return;
            }
            sounds.put(reference, BlobSoundReader.read(section, reference));
            pluginSounds.get(plugin.getName()).add(reference);
        });
    }

    public static boolean loadAndRegisterYamlConfiguration(BlobPlugin plugin, File file) {
        SoundManager manager = BlobLib.getInstance().getSoundManager();
        manager.loadYamlConfiguration(plugin, file);
        manager.duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate BlobSound: '" + key + "' (found " + value + " instances)"));
        return true;
    }

    private void loadYamlConfiguration(File file) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Sound") && !section.isString("Sound"))
                return;
            if (sounds.containsKey(reference)) {
                addDuplicate(reference);
                return;
            }
            sounds.put(reference, BlobSoundReader.read(section, reference));
        });
    }

    public static void continueLoadingSounds(BlobPlugin plugin, boolean warnDuplicates, File... files) {
        SoundManager manager = BlobLib.getInstance().getSoundManager();
        manager.duplicates.clear();
        for (File file : files)
            manager.loadYamlConfiguration(plugin, file);
        if (warnDuplicates)
            manager.duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                    .log("Duplicate BlobSound: '" + key + "' (found " + value + " instances)"));
    }

    private void addDuplicate(String key) {
        if (duplicates.containsKey(key))
            duplicates.put(key, duplicates.get(key) + 1);
        else
            duplicates.put(key, 2);
    }

    @Nullable
    public BlobSound getSound(String key) {
        return sounds.get(key);
    }

    public void play(Player player, String key) {
        BlobSound sound = getSound(key);
        if (sound == null)
            throw new NullPointerException("Sound '" + key + "' does not exist!");
        sound.play(player);
    }
}
