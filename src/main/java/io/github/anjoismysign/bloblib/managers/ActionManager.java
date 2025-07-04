package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.action.Action;
import io.github.anjoismysign.bloblib.entities.DataAssetType;
import io.github.anjoismysign.bloblib.exception.ConfigurationFieldException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ActionManager {
    private final BlobLib main;
    private HashMap<String, Action<Entity>> actions;
    private HashMap<String, Set<String>> pluginActions;
    private HashMap<String, Integer> duplicates;

    public ActionManager() {
        this.main = BlobLib.getInstance();
    }

    public void reload() {
        load();
    }

    public void load() {
        actions = new HashMap<>();
        pluginActions = new HashMap<>();
        duplicates = new HashMap<>();
        loadFiles(main.getFileManager().getDirectory(DataAssetType.ACTION));
        duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate Action: '" + key + "' (found " + value + " instances)"));
    }

    public void load(BlobPlugin plugin, IManagerDirector director) {
        String pluginName = plugin.getName();
        if (pluginActions.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has already been loaded");
        pluginActions.put(pluginName, new HashSet<>());
        duplicates.clear();
        File directory = director.getFileManager().getDirectory(DataAssetType.ACTION);
        loadFiles(directory);
        duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                .log("Duplicate Action: '" + key + "' (found " + value + " instances)"));
    }

    public void unload(BlobPlugin plugin) {
        String pluginName = plugin.getName();
        Set<String> actions = pluginActions.get(pluginName);
        pluginActions.remove(pluginName);
        if (actions == null)
            return;
        actions.forEach(actions::remove);
    }

    public static void unloadBlobPlugin(BlobPlugin plugin) {
        BlobLib.getInstance().getActionManager().unload(plugin);
    }

    public static void loadBlobPlugin(BlobPlugin plugin, IManagerDirector director) {
        ActionManager manager = BlobLib.getInstance().getActionManager();
        manager.load(plugin, director);
    }

    public static void loadBlobPlugin(BlobPlugin plugin) {
        loadBlobPlugin(plugin, plugin.getManagerDirector());
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
                } catch ( ConfigurationFieldException exception ) {
                    main.getLogger().severe(exception.getMessage() + "\nAt: " + file.getPath());
                    continue;
                } catch ( Throwable throwable ) {
                    throwable.printStackTrace();
                    continue;
                }
            }
            if (file.isDirectory())
                loadFiles(file);
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
            if (actions.containsKey(reference)) {
                addDuplicate(reference);
                return;
            }
            actions.put(reference, Action.fromConfigurationSection(section));
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
            if (actions.containsKey(reference)) {
                addDuplicate(reference);
                return;
            }
            actions.put(reference, Action.fromConfigurationSection(section));
            pluginActions.get(plugin.getName()).add(reference);
        });
    }

    /**
     * Loads a YamlConfiguration from a file and registers it to the plugin.
     * Will return false if the plugin has not been loaded.
     * Will print duplicates.
     * NOTE: Printing duplicates runs for each time you call this method!
     *
     * @param file   The file to load
     * @param plugin The plugin to register the actions to
     * @return Whether the plugin has been loaded
     */
    public static boolean loadAndRegisterYamlConfiguration(File file, BlobPlugin plugin) {
        ActionManager manager = BlobLib.getInstance().getActionManager();
        if (!manager.pluginActions.containsKey(plugin.getName()))
            return false;
        manager.loadYamlConfiguration(file, plugin);
        manager.duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate Action: '" + key + "' (found " + value + " instances)"));
        return true;
    }

    private void addDuplicate(String key) {
        if (duplicates.containsKey(key))
            duplicates.put(key, duplicates.get(key) + 1);
        else
            duplicates.put(key, 2);
    }

    public Action<Entity> getAction(String reference) {
        return actions.get(reference);
    }
}