package us.mytheria.bloblib.managers;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.api.BlobLibTranslatableAPI;
import us.mytheria.bloblib.entities.DataAssetType;
import us.mytheria.bloblib.entities.IFileManager;
import us.mytheria.bloblib.entities.translatable.TranslatableBlock;
import us.mytheria.bloblib.entities.translatable.TranslatableReader;
import us.mytheria.bloblib.entities.translatable.TranslatableRegistry;
import us.mytheria.bloblib.entities.translatable.TranslatableSnippet;
import us.mytheria.bloblib.exception.ConfigurationFieldException;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
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
        loadSnippets(main.getFileManager().getDirectory(DataAssetType.TRANSLATABLE_SNIPPET));
        loadBlocks(main.getFileManager().getDirectory(DataAssetType.TRANSLATABLE_BLOCK));
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
        File snippetsDirectory = fileManager.getDirectory(DataAssetType.TRANSLATABLE_SNIPPET);
        loadSnippets(snippetsDirectory);
        File blocksDirectory = fileManager.getDirectory(DataAssetType.TRANSLATABLE_BLOCK);
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

    private void loadSnippets(File directory) {
        @Nullable File[] listOfFiles = directory.listFiles();
        if (listOfFiles == null)
            return;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".yml"))
                    continue;
                try {
                    loadSnippet(file);
                } catch ( ConfigurationFieldException exception ) {
                    main.getLogger().severe(exception.getMessage() + "\nAt: " + file.getPath());
                    continue;
                } catch ( Throwable throwable ) {
                    throwable.printStackTrace();
                    continue;
                }
            }
            if (file.isDirectory())
                loadSnippets(file);
        }
    }

    private void loadBlocks(File directory) {
        @Nullable File[] listOfFiles = directory.listFiles();
        if (listOfFiles == null)
            return;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".yml"))
                    continue;
                try {
                    loadBlock(file);
                } catch ( ConfigurationFieldException exception ) {
                    main.getLogger().severe(exception.getMessage() + "\nAt: " + file.getPath());
                    continue;
                } catch ( Throwable throwable ) {
                    throwable.printStackTrace();
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
        String locale = yamlConfiguration.getString("Locale", "en_us");
        if (yamlConfiguration.isString("Snippet")) {
            addSnippet(fileName, TranslatableReader.SNIPPET(yamlConfiguration,
                    locale, fileName));
            pluginSnippets.get(plugin.getName()).add(fileName);
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.isString("Snippet"))
                return;
            addSnippet(reference, TranslatableReader.SNIPPET(section, locale, reference));
            pluginSnippets.get(plugin.getName()).add(reference);
        });
    }

    private void loadBlock(BlobPlugin plugin, File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String locale = yamlConfiguration.getString("Locale", "en_us");
        if (!yamlConfiguration.getStringList("Block").isEmpty()) {
            addBlock(fileName, TranslatableReader.BLOCK(yamlConfiguration, locale, fileName));
            pluginBlocks.get(plugin.getName()).add(fileName);
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (section.getStringList("Block").isEmpty())
                return;
            addBlock(reference, TranslatableReader.BLOCK(section, locale, reference));
            pluginBlocks.get(plugin.getName()).add(reference);
        });
    }

    public static void continueLoadingSnippets(BlobPlugin plugin, boolean warnDuplicates, File... files) {
        TranslatableManager manager = BlobLib.getInstance().getTranslatableManager();
        manager.duplicates.clear();
        for (File file : files) {
            try {
                manager.loadSnippet(plugin, file);
            } catch ( Throwable e ) {
                e.printStackTrace();
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
            } catch ( Throwable e ) {
                e.printStackTrace();
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
        String locale = yamlConfiguration.getString("Locale", "en_us");
        if (yamlConfiguration.isString("Snippet")) {
            addSnippet(fileName, TranslatableReader.SNIPPET(yamlConfiguration, locale, fileName));
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (!section.isString("Snippet"))
                return;
            addSnippet(reference, TranslatableReader.SNIPPET(section, locale, reference));
        });
    }

    private void loadBlock(File file) {
        String fileName = FilenameUtils.removeExtension(file.getName());
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String locale = yamlConfiguration.getString("Locale", "en_us");
        if (!yamlConfiguration.getStringList("Block").isEmpty()) {
            addBlock(fileName, TranslatableReader.BLOCK(yamlConfiguration, locale, fileName));
            return;
        }
        yamlConfiguration.getKeys(true).forEach(reference -> {
            if (!yamlConfiguration.isConfigurationSection(reference))
                return;
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(reference);
            if (section.getStringList("Block").isEmpty())
                return;
            addBlock(reference, TranslatableReader.BLOCK(section, locale, reference));
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
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        TranslatableSnippet snippet = registry.get(locale);
        if (snippet == null)
            return registry.getDefault();
        Objects.requireNonNull(snippet, "Snippet '" + key + "' does not have a default locale and is not available in '" + locale + "'");
        return snippet;
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
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        TranslatableBlock block = registry.get(locale);
        if (block == null)
            return registry.getDefault();
        Objects.requireNonNull(block, "Block '" + key + "' does not have a default locale and is not available in '" + locale + "'");
        return block;
    }

    @Nullable
    public TranslatableBlock getBlock(String key) {
        TranslatableRegistry<TranslatableBlock> registry = getBlockRegistry(key);
        if (registry == null)
            return null;
        return registry.getDefault();
    }

    private void addSnippet(String key, TranslatableSnippet snippet) {
        TranslatableRegistry<TranslatableSnippet> registry = snippets.get(key);
        if (registry == null)
            registry = TranslatableRegistry.of("en_us", key);
        if (!registry.process(snippet)) {
            addDuplicate(key);
            return;
        }
        BlobLib.getAnjoLogger().debug("loaded Snippet: " + key + " with locale: " + snippet.locale());
        snippets.put(key, registry);
    }

    private void addBlock(String key, TranslatableBlock block) {
        TranslatableRegistry<TranslatableBlock> registry = blocks.get(key);
        if (registry == null)
            registry = TranslatableRegistry.of("en_us", key);
        if (!registry.process(block)) {
            addDuplicate(key);
            return;
        }
        BlobLib.getAnjoLogger().debug("loaded Block: " + key + " with locale: " + block.locale());
        blocks.put(key, registry);
    }
}
