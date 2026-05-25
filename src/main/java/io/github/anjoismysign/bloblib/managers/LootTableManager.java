package io.github.anjoismysign.bloblib.managers;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.entities.DataAssetType;
import io.github.anjoismysign.bloblib.entities.loot.LootEntry;
import io.github.anjoismysign.bloblib.entities.loot.LootEntryType;
import io.github.anjoismysign.bloblib.entities.loot.LootFunction;
import io.github.anjoismysign.bloblib.entities.loot.LootFunctionType;
import io.github.anjoismysign.bloblib.entities.loot.LootPool;
import io.github.anjoismysign.bloblib.entities.loot.LootTable;
import io.github.anjoismysign.bloblib.entities.loot.LootTableIO;
import io.github.anjoismysign.bloblib.entities.translatable.TranslatableItem;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class LootTableManager {

    private static final Random RANDOM = new Random();

    private final BlobLib main;
    private final File lootTableDirectory;
    private Map<String, LootTable> lootTables;
    private Map<String, Set<String>> pluginAssets;

    public LootTableManager(@NotNull BlobLib main) {
        this.main = main;
        this.lootTableDirectory = new File(main.getDataFolder()
                + DataAssetType.LOOT_TABLE.getDirectoryPath());
        this.lootTableDirectory.mkdirs();
    }

    public void reload() {
        lootTables = new HashMap<>();
        pluginAssets = new HashMap<>();
        loadFiles(lootTableDirectory);
        writeDefaultExample();
    }

    public void reload(@NotNull BlobPlugin plugin, @NotNull IManagerDirector director) {
        if (pluginAssets.containsKey(plugin.getName()))
            throw new IllegalArgumentException(
                    "Plugin '" + plugin.getName() + "' has already been loaded");
        pluginAssets.put(plugin.getName(), new HashSet<>());
        File directory = director.getFileManager().getDirectory(DataAssetType.LOOT_TABLE);
        loadFiles(directory, plugin);
    }

    public void unload(@NotNull BlobPlugin plugin) {
        Set<String> keys = pluginAssets.remove(plugin.getName());
        if (keys != null)
            keys.forEach(lootTables::remove);
    }

    public void continueLoadingAssets(@NotNull BlobPlugin plugin,
                                       boolean warnDuplicates,
                                       @NotNull File... files) {
        for (File file : files) {
            loadFile(file, plugin);
        }
    }

    @Nullable
    public LootTable getLootTable(@NotNull String identifier) {
        Objects.requireNonNull(identifier);
        return lootTables.get(identifier);
    }

    @NotNull
    public Map<String, LootTable> getLootTables() {
        return Map.copyOf(lootTables);
    }

    public boolean giveToInventory(@NotNull Inventory inventory, @NotNull String identifier, @Nullable String locale){
        Objects.requireNonNull(inventory);
        Objects.requireNonNull(identifier);
        LootTable table = lootTables.get(identifier);
        if (table == null) {
            main.getLogger().info("LootTable not found: '" + identifier + "'");
            return false;
        }
        for (LootPool pool : table.pools()) {
            for (int i = 0; i < pool.rolls(); i++) {
                LootEntry entry = pickEntry(pool);
                if (entry == null)
                    continue;
                ItemStack itemStack = resolveEntry(entry);
                if (itemStack == null) {
                    continue;
                }
                itemStack = applyFunctions(itemStack, entry.functions());
                if (itemStack == null){
                    continue;
                }
                if (locale != null) {
                    TranslatableItem.localize(itemStack, locale);
                }
                inventory.addItem(itemStack);
            }
        }
        return true;
    }

    public boolean spawn(@NotNull Location location, @NotNull String identifier) {
        Objects.requireNonNull(location);
        Objects.requireNonNull(identifier);
        LootTable table = lootTables.get(identifier);
        if (table == null) {
            main.getLogger().info("LootTable not found: '" + identifier + "'");
            return false;
        }
        World world = location.getWorld();
        if (world == null)
            return false;
        for (LootPool pool : table.pools()) {
            for (int i = 0; i < pool.rolls(); i++) {
                LootEntry entry = pickEntry(pool);
                if (entry == null)
                    continue;
                ItemStack itemStack = resolveEntry(entry);
                if (itemStack == null) {
                    continue;
                }
                itemStack = applyFunctions(itemStack, entry.functions());
                world.dropItemNaturally(location, itemStack);
            }
        }
        return true;
    }

    @Nullable
    private LootEntry pickEntry(@NotNull LootPool pool) {
        int totalWeight = 0;
        for (LootEntry entry : pool.entries())
            totalWeight += entry.weight();
        if (totalWeight <= 0)
            return null;
        int roll = RANDOM.nextInt(totalWeight);
        int cumulative = 0;
        for (LootEntry entry : pool.entries()) {
            cumulative += entry.weight();
            if (roll < cumulative)
                return entry;
        }
        return null;
    }

    @Nullable
    private ItemStack resolveEntry(@NotNull LootEntry entry) {
        return switch (entry.type()) {
            case EMPTY -> null;
            case ITEM -> {
                var itemRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);
                String itemName = entry.name();
                if (itemName == null) {
                    yield null;
                }
                try {
                    @Nullable ItemType itemType = itemRegistry.get(Key.key(itemName));
                    if (itemType == null){
                        yield null;
                    }
                    yield itemType.createItemStack();
                } catch (IllegalArgumentException e) {
                    main.getLogger().info("Unknown material: '" + itemName + "'");
                    yield null;
                }
            }
            case TRANSLATABLE_ITEM -> {
                if (entry.name() == null) {
                    yield null;
                }
                TranslatableItem translatableItem = TranslatableItem.by(entry.name());
                if (translatableItem == null) {
                    main.getLogger().info("TranslatableItem not found: '" + entry.name() + "'");
                    yield null;
                }
                yield translatableItem.getClone();
            }
        };
    }

    @Nullable
    private ItemStack applyFunctions(@Nullable ItemStack itemStack,
                                      @Nullable List<LootFunction> functions) {
        if (itemStack == null || functions == null || functions.isEmpty())
            return itemStack;
        for (LootFunction function : functions) {
            switch (function.type()) {
                case SET_COUNT -> {
                    int min = ((Number) function.data().getOrDefault("min", 1)).intValue();
                    int max = ((Number) function.data().getOrDefault("max", 1)).intValue();
                    int count = min + RANDOM.nextInt(max - min + 1);
                    itemStack.setAmount(count);
                }
            }
        }
        return itemStack;
    }

    private void loadFiles(@NotNull File directory) {
        File[] files = directory.listFiles();
        if (files == null)
            return;
        for (File file : files) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".json"))
                    continue;
                loadFile(file, null);
            }
            if (file.isDirectory())
                loadFiles(file);
        }
    }

    private void loadFiles(@NotNull File directory, @NotNull BlobPlugin plugin) {
        File[] files = directory.listFiles();
        if (files == null)
            return;
        for (File file : files) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".json"))
                    continue;
                loadFile(file, plugin);
            }
            if (file.isDirectory())
                loadFiles(file, plugin);
        }
    }

    private void loadFile(@NotNull File file, @Nullable BlobPlugin plugin) {
        String name = file.getName();
        if (name.equals(".DS_Store"))
            return;
        String identifier = name.substring(0, name.lastIndexOf('.'));
        LootTable table = LootTableIO.read(file, identifier);
        if (table == null)
            return;
        if (lootTables.containsKey(identifier)) {
            main.getLogger().info("Duplicate LootTable: '" + identifier + "' at " + file.getPath());
            return;
        }
        lootTables.put(identifier, table);
        if (plugin != null)
            pluginAssets.get(plugin.getName()).add(identifier);
    }

    private void writeDefaultExample() {
        var fileName = "BlobLib.Example.json";
        File example = new File(lootTableDirectory, fileName);
        if (example.isFile())
            return;
        try (InputStream in = main.getResource(fileName)) {
            if (in == null)
                return;
            example.getParentFile().mkdirs();
            Files.copy(in, example.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
