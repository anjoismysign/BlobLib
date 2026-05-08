package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.entities.BlobSoundReader;
import io.github.anjoismysign.bloblib.entities.DataAssetType;
import io.github.anjoismysign.bloblib.entities.message.BlobSound;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SoundManager {
    private final BlobLib main;
    private Map<String, BlobSound> sounds;
    private Map<String, Set<String>> pluginSounds;
    private Map<String, List<String>> duplicates;
    private Map<String, String> keyFirstFile;

    public static void loadBlobPlugin(BlobPlugin plugin, IManagerDirector director) {
        SoundManager manager = BlobLib.getInstance().getSoundManager();
        manager.load(plugin, director);
    }

    public static void unloadBlobPlugin(BlobPlugin plugin) {
        SoundManager manager = BlobLib.getInstance().getSoundManager();
        manager.unload(plugin);
    }

    public static boolean loadAndRegisterYamlConfiguration(BlobPlugin plugin, File file) {
        SoundManager manager = BlobLib.getInstance().getSoundManager();
        manager.loadYamlConfiguration(plugin, file);
        manager.duplicates.forEach((key, paths) -> BlobLib.getAnjoLogger()
                .log("Duplicate BlobSound: '" + key + "' (found " + paths.size() + " instances)\n" +
                        paths.stream().map(p -> "  - " + p).collect(Collectors.joining("\n"))));
        return true;
    }

    public static void continueLoadingSounds(BlobPlugin plugin, boolean warnDuplicates, File... files) {
        SoundManager manager = BlobLib.getInstance().getSoundManager();
        manager.duplicates.clear();
        for (File file : files)
            manager.loadYamlConfiguration(plugin, file);
        if (warnDuplicates)
            manager.duplicates.forEach((key, paths) -> plugin.getAnjoLogger()
                    .log("Duplicate BlobSound: '" + key + "' (found " + paths.size() + " instances)\n" +
                            paths.stream().map(p -> "  - " + p).collect(Collectors.joining("\n"))));
    }

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
        keyFirstFile = new HashMap<>();
        loadFiles(main.getFileManager().getDirectory(DataAssetType.BLOB_SOUND));
        duplicates.forEach((key, paths) -> main.getLogger()
                .severe("Duplicate BlobSound: '" + key + "' (found " + paths.size() + " instances)\n" +
                        paths.stream().map(p -> "  - " + p).collect(Collectors.joining("\n"))));
    }

    public void load(BlobPlugin plugin, IManagerDirector director) {
        String pluginName = plugin.getName();
        if (pluginSounds.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has already been loaded");
        pluginSounds.put(pluginName, new HashSet<>());
        duplicates.clear();
        File directory = director.getFileManager().getDirectory(DataAssetType.BLOB_SOUND);
        loadFiles(directory);
        duplicates.forEach((key, paths) -> main.getLogger()
                .severe("Duplicate BlobSound: '" + key + "' (found " + paths.size() + " instances)\n" +
                        paths.stream().map(p -> "  - " + p).collect(Collectors.joining("\n"))));
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

    private void loadFiles(File directory) {
        @Nullable File[] listOfFiles = directory.listFiles();
        if (listOfFiles == null)
            return;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().equals(".DS_Store"))
                    continue;
                try {
                    loadYamlConfiguration(file);
                } catch (ConfigurationFieldException exception) {
                    main.getLogger().severe(exception.getMessage() + "\nAt: " + file.getPath());
                    continue;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    continue;
                }
            }
            if (file.isDirectory())
                loadFiles(file);
        }
    }

    private void loadYamlConfiguration(BlobPlugin plugin, File file) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String filePath = file.getPath();
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Sound") && !section.isString("Sound"))
                return;
            if (sounds.containsKey(reference)) {
                addDuplicate(reference, filePath);
                return;
            }
            sounds.put(reference, BlobSoundReader.read(section, reference));
            keyFirstFile.put(reference, filePath);
            pluginSounds.get(plugin.getName()).add(reference);
        });
    }

    private void loadYamlConfiguration(File file) {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String filePath = file.getPath();
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Sound") && !section.isString("Sound"))
                return;
            if (sounds.containsKey(reference)) {
                addDuplicate(reference, filePath);
                return;
            }
            try {
                BlobSound sound = BlobSoundReader.read(section, reference);
                sounds.put(reference, sound);
                keyFirstFile.put(reference, filePath);
            } catch (Throwable throwable) {
                main.getLogger().severe(throwable.getMessage() + "\nAt: " + filePath);
            }
        });
    }

    private void addDuplicate(String key, String filePath) {
        duplicates.computeIfAbsent(key, k -> {
            List<String> list = new ArrayList<>();
            list.add(keyFirstFile.getOrDefault(k, "unknown"));
            return list;
        }).add(filePath);
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

    public Map<String, BlobSound> getSounds() {
        return Map.copyOf(sounds);
    }
}
