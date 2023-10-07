package us.mytheria.bloblib.managers;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.IFileManager;
import us.mytheria.bloblib.entities.translatable.TranslatableBlock;
import us.mytheria.bloblib.entities.translatable.TranslatableReader;
import us.mytheria.bloblib.entities.translatable.TranslatableRegistry;
import us.mytheria.bloblib.entities.translatable.TranslatableSnippet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TranslatableManager {
    private final BlobLib main;
    private HashMap<String, TranslatableRegistry<TranslatableSnippet>> snippets;
    private HashMap<String, TranslatableRegistry<TranslatableBlock>> blocks;
    private HashMap<String, Set<String>> pluginSnippets;
    private HashMap<String, Set<String>> pluginBlocks;
    private HashMap<String, Integer> duplicates;

    public TranslatableManager() {
        this.main = BlobLib.getInstance();
    }

    public void reload() {
        load();
    }

    public void load() {
        snippets = new HashMap<>();
        blocks = new HashMap<>();
        pluginSnippets = new HashMap<>();
        pluginBlocks = new HashMap<>();
        duplicates = new HashMap<>();
        loadSnippets(main.getFileManager().snippetsDirectory());
        loadBlocks(main.getFileManager().blocksDirectory());
        duplicates.forEach((key, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate Inventory: '" + key + "' (found " + value + " instances)"));
    }

    public void load(BlobPlugin plugin, IManagerDirector director) {
        String pluginName = plugin.getName();
        if (pluginSnippets.containsKey(pluginName))
            throw new IllegalArgumentException("Plugin '" + pluginName + "' has already been loaded");
        pluginSnippets.put(pluginName, new HashSet<>());
        pluginBlocks.put(pluginName, new HashSet<>());
        duplicates.clear();
        IFileManager fileManager = director.getFileManager();
        File snippetsDirectory = fileManager.translatableSnippetsDirectory();
        loadSnippets(snippetsDirectory);
        File blocksDirectory = fileManager.translatableBlocksDirectory();
        loadBlocks(blocksDirectory);
        duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                .log("Duplicate translatable: '" + key + "' (found " + value + " instances)"));
    }

    public static void loadBlobPlugin(BlobPlugin plugin, IManagerDirector director) {
        TranslatableManager manager = BlobLib.getInstance().getTranslatableManager();
        manager.load(plugin, director);
    }

    public static void loadBlobPlugin(BlobPlugin plugin) {
        loadBlobPlugin(plugin, plugin.getManagerDirector());
    }

    public void unload(BlobPlugin plugin) {
        String pluginName = plugin.getName();
        Set<String> snippets = pluginSnippets.get(pluginName);
        if (snippets == null)
            return;
        Iterator<String> iterator = snippets.iterator();
        while (iterator.hasNext()) {
            String inventoryName = iterator.next();
            this.snippets.remove(inventoryName);
            iterator.remove();
        }
        Set<String> blocks = pluginBlocks.get(pluginName);
        iterator = blocks.iterator();
        while (iterator.hasNext()) {
            String inventoryName = iterator.next();
            this.blocks.remove(inventoryName);
            iterator.remove();
        }
        pluginSnippets.remove(pluginName);
        pluginBlocks.remove(pluginName);
    }

    public static void unloadBlobPlugin(BlobPlugin plugin) {
        TranslatableManager manager = BlobLib.getInstance().getTranslatableManager();
        manager.unload(plugin);
    }

    private void loadSnippets(File path) {
        File[] listOfFiles = path.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".yml"))
                    continue;
                try {
                    loadSnippet(file);
                } catch (Throwable e) {
                    e.printStackTrace();
                    continue;
                }
            }
            if (file.isDirectory())
                loadSnippets(file);
        }
    }

    private void loadBlocks(File path) {
        File[] listOfFiles = path.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".yml"))
                    continue;
                try {
                    loadBlock(file);
                } catch (Throwable e) {
                    e.printStackTrace();
                    continue;
                }
            }
            if (file.isDirectory())
                loadBlocks(file);
        }
    }

    private void loadSnippet(BlobPlugin plugin, File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            addSnippet(fileName, TranslatableReader.SNIPPET(yamlConfiguration, fileName));
            pluginSnippets.get(plugin.getName()).add(fileName);
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            addSnippet(reference, TranslatableReader.SNIPPET(section, reference));
            pluginSnippets.get(plugin.getName()).add(reference);
        });
    }

    private void loadBlock(BlobPlugin plugin, File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            addBlock(fileName, TranslatableReader.BLOCK(yamlConfiguration, fileName));
            pluginBlocks.get(plugin.getName()).add(fileName);
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            addBlock(reference, TranslatableReader.BLOCK(section, reference));
            pluginBlocks.get(plugin.getName()).add(reference);
        });
    }

    public static void continueLoadingSnippets(BlobPlugin plugin, boolean warnDuplicates, File... files) {
        TranslatableManager manager = BlobLib.getInstance().getTranslatableManager();
        manager.duplicates.clear();
        for (File file : files) {
            try {
                manager.loadSnippet(plugin, file);
            } catch (Throwable e) {
                e.printStackTrace();
                continue;
            }
        }
        if (warnDuplicates)
            manager.duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                    .log("Duplicate BlobInventory: '" + key + "' (found " + value + " instances)"));
    }

    public static void continueLoadingSnippets(BlobPlugin plugin, File... files) {
        continueLoadingSnippets(plugin, true, files);
    }

    public static void continueLoadingBlocks(BlobPlugin plugin, boolean warnDuplicates, File... files) {
        TranslatableManager manager = BlobLib.getInstance().getTranslatableManager();
        manager.duplicates.clear();
        for (File file : files) {
            try {
                manager.loadBlock(plugin, file);
            } catch (Throwable e) {
                e.printStackTrace();
                continue;
            }
        }
        if (warnDuplicates)
            manager.duplicates.forEach((key, value) -> plugin.getAnjoLogger()
                    .log("Duplicate MetaBlobInventory: '" + key + "' (found " + value + " instances)"));

    }

    public static void continueLoadingBlocks(BlobPlugin plugin, File... files) {
        continueLoadingBlocks(plugin, true, files);
    }

    private void loadSnippet(File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            addSnippet(fileName, TranslatableReader.SNIPPET(yamlConfiguration, fileName));
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            addSnippet(reference, TranslatableReader.SNIPPET(section, reference));
        });
    }

    private void loadBlock(File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (yamlConfiguration.contains("Size") && yamlConfiguration.isInt("Size")) {
            addBlock(fileName, TranslatableReader.BLOCK(yamlConfiguration, fileName));
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.contains("Size") && !section.isInt("Size"))
                return;
            addBlock(reference, TranslatableReader.BLOCK(section, reference));
        });
    }


    private void addDuplicate(String key) {
        if (duplicates.containsKey(key))
            duplicates.put(key, duplicates.get(key) + 1);
        else
            duplicates.put(key, 2);
    }

    @Nullable
    public TranslatableRegistry<TranslatableSnippet> getSnippetRegistry(String key) {
        return snippets.get(key);
    }

    @Nullable
    public TranslatableSnippet getSnippet(String key, String locale) {
        TranslatableRegistry<TranslatableSnippet> registry = getSnippetRegistry(key);
        if (registry == null)
            return null;
        return registry.get(locale);
    }

    @Nullable
    public TranslatableSnippet getSnippet(String key) {
        TranslatableRegistry<TranslatableSnippet> registry = getSnippetRegistry(key);
        if (registry == null)
            return null;
        return registry.getDefault();
    }

    @Nullable
    public TranslatableRegistry<TranslatableBlock> getBlockRegistry(String key) {
        return blocks.get(key);
    }

    @Nullable
    public TranslatableBlock getBlock(String key, String locale) {
        TranslatableRegistry<TranslatableBlock> registry = getBlockRegistry(key);
        if (registry == null)
            return null;
        return registry.get(locale);
    }

    @Nullable
    public TranslatableBlock getBlock(String key) {
        TranslatableRegistry<TranslatableBlock> registry = getBlockRegistry(key);
        if (registry == null)
            return null;
        return registry.getDefault();
    }

    private void addSnippet(String key, TranslatableSnippet inventory) {
        TranslatableRegistry<TranslatableSnippet> registry = snippets.get(key);
        if (registry == null)
            registry = TranslatableRegistry.of("en_us", key);
        if (!registry.process(inventory)) {
            addDuplicate(key);
            return;
        }
        snippets.put(key, registry);
    }

    private void addBlock(String key, TranslatableBlock inventory) {
        TranslatableRegistry<TranslatableBlock> registry = blocks.get(key);
        if (registry == null)
            registry = TranslatableRegistry.of("en_us", key);
        if (!registry.process(inventory)) {
            addDuplicate(key);
            return;
        }
        blocks.put(key, registry);
    }
}
