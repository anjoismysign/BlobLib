package us.mytheria.bloblib.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.BlobSoundReader;
import us.mytheria.bloblib.entities.message.BlobSound;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
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

    public void load(BlobPlugin plugin, ManagerDirector director) {
        String pluginName = plugin.getName();
        if (pluginSounds.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has already been loaded");
        pluginSounds.put(pluginName, new HashSet<>());
        duplicates.clear();
        File directory = director.getFileManager().soundsDirectory();
        loadFiles(plugin, directory);
        duplicates.forEach((key, value) -> main.getLogger()
                .severe("Duplicate BlobSound: '" + key + "' (found " + value + " instances)"));
    }

    public static void loadBlobPlugin(BlobPlugin plugin, ManagerDirector director) {
        SoundManager manager = BlobLib.getInstance().getSoundManager();
        manager.load(plugin, director);
    }

    public void unload(BlobPlugin plugin) {
        String pluginName = plugin.getName();
        if (!pluginSounds.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has not been loaded");
        pluginSounds.get(pluginName).forEach(sounds::remove);
        pluginSounds.remove(pluginName);
    }

    public static void unloadBlobPlugin(BlobPlugin plugin) {
        SoundManager manager = BlobLib.getInstance().getSoundManager();
        manager.unload(plugin);
    }

    private void loadFiles(BlobPlugin plugin, File path) {
        File[] listOfFiles = path.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().equals(".DS_Store"))
                    continue;
                loadYamlConfiguration(plugin, file);
            }
            if (file.isDirectory())
                loadFiles(path);
        }
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

    private void loadYamlConfiguration(BlobPlugin plugin, File file) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.getKeys(false).forEach(key -> {
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(key);
            section.getKeys(true).forEach(subKey -> {
                if (!section.isConfigurationSection(subKey))
                    return;
                ConfigurationSection subSection = section.getConfigurationSection(subKey);
                if (!subSection.isString("Sound"))
                    return;
                String reference = key + "." + subKey;
                if (sounds.containsKey(reference)) {
                    addDuplicate(reference);
                    return;
                }
                sounds.put(reference, BlobSoundReader.read(subSection));
                pluginSounds.get(plugin.getName()).add(reference);
            });
        });
    }

    private void loadYamlConfiguration(File file) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.getKeys(false).forEach(key -> {
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(key);
            section.getKeys(true).forEach(subKey -> {
                if (!section.isConfigurationSection(subKey))
                    return;
                ConfigurationSection subSection = section.getConfigurationSection(subKey);
                if (!subSection.isString("Sound"))
                    return;
                String mapKey = key + "." + subKey;
                if (sounds.containsKey(mapKey)) {
                    addDuplicate(mapKey);
                    return;
                }
                sounds.put(key + "." + subKey, BlobSoundReader.read(subSection));
            });
        });
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
