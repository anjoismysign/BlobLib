package io.github.anjoismysign.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

/**
 * @author anjoismysign
 * <p>
 * An InventoryHolderBuilder is an instance of an InventoryBuilder.
 * It's a wrapper for InventoryHolder's using the BlobLib API.
 */
public class PlayerInventoryBuilder<T extends InventoryButton> extends InventoryBuilder<T> {
    private final UUID playerUniqueId;

    /**
     * Constructs an InventoryHolderBuilder with a title, size, and a ButtonManager.
     *
     * @param title         The title of the inventory.
     * @param size          The size of the inventory.
     * @param buttonManager The ButtonManager that will be used to manage the buttons.
     */
    protected PlayerInventoryBuilder(@NotNull String title, int size,
                                     @NotNull ButtonManager<T> buttonManager,
                                     @NotNull UUID playerUniqueId) {
        Player player = Bukkit.getPlayer(playerUniqueId);
        Objects.requireNonNull(player, "Player with UUID " + playerUniqueId + " is not online!");
        this.playerUniqueId = playerUniqueId;
        this.setTitle(Objects.requireNonNull(title,
                "'title' cannot be null!"));
        this.setSize(size);
        this.setButtonManager(Objects.requireNonNull(buttonManager,
                "'buttonManager' cannot be null!"));
        this.buildInventory();
        this.loadDefaultButtons();
    }

    /**
     * Copies itself to a new reference.
     *
     * @return The cloned inventory
     */
    @NotNull
    public PlayerInventoryBuilder<T> copy() {
        return new PlayerInventoryBuilder<>(getTitle(), getSize(), getButtonManager(), getPlayerUniqueId());
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(playerUniqueId);
    }

    @Override
    @Nullable
    public Inventory getInventory() {
        Player holder = getPlayer();
        if (holder == null)
            return null;
        return holder.getInventory();
    }

    /**
     * Retrieves the inventory holder unique id
     *
     * @return The UUID
     */
    @NotNull
    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

}