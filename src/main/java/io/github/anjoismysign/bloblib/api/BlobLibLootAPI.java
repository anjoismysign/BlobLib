package io.github.anjoismysign.bloblib.api;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.entities.BlobMessageModder;
import io.github.anjoismysign.bloblib.entities.loot.LootTable;
import io.github.anjoismysign.bloblib.entities.message.BlobMessage;
import io.github.anjoismysign.bloblib.managers.BlobLibConfigManager;
import io.github.anjoismysign.bloblib.managers.LocalizableDataAssetManager;
import io.github.anjoismysign.bloblib.managers.LootTableManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class BlobLibLootAPI {
    private static BlobLibLootAPI instance;
    private final BlobLib plugin;

    private BlobLibLootAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibLootAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibLootAPI.instance = new BlobLibLootAPI(plugin);
        }
        return instance;
    }

    public static BlobLibLootAPI getInstance() {
        return getInstance(null);
    }

    private LootTableManager getLootTableManager() {
        return plugin.getLootTableManager();
    }

    /**
     * Gets all registered loot tables.
     *
     * @return a map containing loot table identifiers mapped to their corresponding {@link LootTable}
     */
    @NotNull
    public Map<String, LootTable> getLootTables() {
        return getLootTableManager().getLootTables();
    }

    /**
     * Spawns the contents of a loot table at the given location.
     *
     * @param location the location where the loot should be spawned
     * @param identifier the identifier of the loot table to spawn
     * @return {@code true} if the loot table was successfully spawned, otherwise {@code false}
     */
    public boolean spawn(@NotNull Location location, @NotNull String identifier){
        return getLootTableManager().spawn(location, identifier);
    }

    /**
     * Gives the contents of a loot table directly into an inventory.
     *
     * @param inventory the inventory that should receive the loot
     * @param identifier the identifier of the loot table
     * @param locale the locale context for localized loot generation
     * @return {@code true} if the loot was successfully added, otherwise {@code false}
     */
    public boolean giveToInventory(@NotNull Inventory inventory, @NotNull String identifier, @Nullable String locale){
        return getLootTableManager().giveToInventory(inventory, identifier, locale);
    }

}
