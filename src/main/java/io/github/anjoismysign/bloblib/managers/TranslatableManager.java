package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibTranslatableAPI;
import io.github.anjoismysign.bloblib.entities.DataAssetType;
import io.github.anjoismysign.bloblib.entities.IFileManager;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableBlock;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableReader;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableRegistry;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableSnippet;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static void loadBlobPlugin(BlobPlugin plugin, IManagerDirector director) {
        TranslatableManager manager = BlobLib.getInstance().getTranslatableManager();
        manager.load(plugin, director);
    }

    public static void loadBlobPlugin(BlobPlugin plugin) {
        loadBlobPlugin(plugin, plugin.getManagerDirector());
    }

    public static void unloadBlobPlugin(BlobPlugin plugin) {
        TranslatableManager manager = BlobLib.getInstance().getTranslatableManager();
        manager.unload(plugin);
    }

    public static void continueLoadingSnippets(BlobPlugin plugin, boolean warnDuplicates, File... files) {
        TranslatableManager manager = BlobLib.getInstance().getTranslatableManager();
        manager.duplicates.clear();
        for (File file : files) {
            try {
                manager.loadSnippet(plugin, file);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        if (warnDuplicates)
            manager.duplicates.forEach((identifier, value) -> plugin.getAnjoLogger()
                    .log("Duplicate BlobInventory: '" + identifier + "' (found " + value + " instances)"));
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
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        if (warnDuplicates)
            manager.duplicates.forEach((identifier, value) -> plugin.getAnjoLogger()
                    .log("Duplicate MetaBlobInventory: '" + identifier + "' (found " + value + " instances)"));

    }

    public static void continueLoadingBlocks(BlobPlugin plugin, File... files) {
        continueLoadingBlocks(plugin, true, files);
    }

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
        duplicates.forEach((identifier, value) -> BlobLib.getAnjoLogger()
                .log("Duplicate translatable: '" + identifier + "' (found " + value + " instances)"));
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
        duplicates.forEach((identifier, value) -> plugin.getAnjoLogger()
                .log("Duplicate translatable: '" + identifier + "' (found " + value + " instances)"));
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

    public void saveSnippet(@NotNull File file,
                            @NotNull TranslatableSnippet snippet) {
        Objects.requireNonNull(file, "'file' cannot be null");
        Objects.requireNonNull(snippet, "'snippet' cannot be null");
        if (!file.getName().endsWith(".yml"))
            return;
        File directory = file.getParentFile();
        if (!directory.isDirectory())
            directory.mkdirs();
        try {
            if (!file.isFile()) {
                file.createNewFile();
            }
            String identifier = file.getName().replace(".yml", "");
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            configuration.set("Locale", snippet.locale());
            configuration.set("Snippet", snippet.get().replace(ChatColor.COLOR_CHAR, '&'));
            configuration.save(file);
            addSnippet(identifier, snippet);
        } catch (Throwable throwable) {
            main.getLogger().severe(throwable.getMessage() + "\nAt: " + file.getPath());
        }
    }

    public void saveBlock(@NotNull File file,
                          @NotNull TranslatableBlock block) {
        Objects.requireNonNull(file, "'file' cannot be null");
        Objects.requireNonNull(block, "'block' cannot be null");
        if (!file.getName().endsWith(".yml"))
            return;
        File directory = file.getParentFile();
        if (!directory.isDirectory())
            directory.mkdirs();
        try {
            if (!file.isFile()) {
                file.createNewFile();
            }
            String identifier = file.getName().replace(".yml", "");
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            configuration.set("Locale", block.locale());
            configuration.set("Block", block.get().stream().map(line -> line.replace(ChatColor.COLOR_CHAR, '&')).toList());
            configuration.save(file);
            addBlock(identifier, block);
        } catch (Throwable throwable) {
            main.getLogger().severe(throwable.getMessage() + "\nAt: " + file.getPath());
        }
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
                } catch (Throwable throwable) {
                    main.getLogger().severe(throwable.getMessage() + "\nAt: " + file.getPath());
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
                } catch (Throwable throwable) {
                    main.getLogger().severe(throwable.getMessage() + "\nAt: " + file.getPath());
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


    private void addDuplicate(String identifier) {
        if (duplicates.containsKey(identifier))
            duplicates.put(identifier, duplicates.get(identifier) + 1);
        else
            duplicates.put(identifier, 2);
    }

    @Nullable
    public TranslatableRegistry<TranslatableSnippet> getSnippetRegistry(String identifier) {
        return snippets.get(identifier);
    }

    @Nullable
    public TranslatableSnippet getSnippet(String identifier, String locale) {
        TranslatableRegistry<TranslatableSnippet> registry = getSnippetRegistry(identifier);
        if (registry == null)
            return null;
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        TranslatableSnippet snippet = registry.get(locale);
        if (snippet == null)
            return registry.getDefault();
        Objects.requireNonNull(snippet, "Snippet '" + identifier + "' does not have a default locale and is not available in '" + locale + "'");
        return snippet;
    }

    @Nullable
    public TranslatableSnippet getSnippet(String identifier) {
        TranslatableRegistry<TranslatableSnippet> registry = getSnippetRegistry(identifier);
        if (registry == null)
            return null;
        return registry.getDefault();
    }

    @Nullable
    public TranslatableRegistry<TranslatableBlock> getBlockRegistry(String identifier) {
        return blocks.get(identifier);
    }

    @Nullable
    public TranslatableBlock getBlock(String identifier, String locale) {
        TranslatableRegistry<TranslatableBlock> registry = getBlockRegistry(identifier);
        if (registry == null)
            return null;
        locale = BlobLibTranslatableAPI.getInstance().getRealLocale(locale);
        TranslatableBlock block = registry.get(locale);
        if (block == null)
            return registry.getDefault();
        Objects.requireNonNull(block, "Block '" + identifier + "' does not have a default locale and is not available in '" + locale + "'");
        return block;
    }

    @Nullable
    public TranslatableBlock getBlock(String identifier) {
        TranslatableRegistry<TranslatableBlock> registry = getBlockRegistry(identifier);
        if (registry == null)
            return null;
        return registry.getDefault();
    }

    private void addSnippet(String identifier, TranslatableSnippet snippet) {
        TranslatableRegistry<TranslatableSnippet> registry = snippets.get(identifier);
        if (registry == null)
            registry = TranslatableRegistry.of("en_us", identifier);
        if (!registry.process(snippet)) {
            addDuplicate(identifier);
            return;
        }
        BlobLib.getAnjoLogger().debug("loaded Snippet: " + identifier + " with locale: " + snippet.locale());
        snippets.put(identifier, registry);
    }

    private void addBlock(String identifier, TranslatableBlock block) {
        TranslatableRegistry<TranslatableBlock> registry = blocks.get(identifier);
        if (registry == null)
            registry = TranslatableRegistry.of("en_us", identifier);
        if (!registry.process(block)) {
            addDuplicate(identifier);
            return;
        }
        BlobLib.getAnjoLogger().debug("loaded Block: " + identifier + " with locale: " + block.locale());
        blocks.put(identifier, registry);
    }
}
