package us.mytheria.bloblib.displayentity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;

public class ItemDisplayFloatingPet extends DisplayFloatingPet<ItemDisplay, ItemStack> {
    /**
     * Creates a pet
     *
     * @param owner      - the FloatingPet owner
     * @param display    - the display (like BlockData/ItemStack)
     *                   (must be an item or a block)
     * @param particle   - the Particle to itemStack
     * @param customName - the CustomName of the pet
     *                   (if null will be used 'owner's Pet')
     */
    public ItemDisplayFloatingPet(Player owner, ItemStack display, @Nullable Particle particle, @Nullable String customName) {
        super(owner, display, particle, customName);
    }

    void spawnEntity(Location location) {
        BlobLib plugin = BlobLib.getInstance();
        entity = (ItemDisplay) location.getWorld().spawnEntity(location,
                EntityType.ITEM_DISPLAY);
        setCustomName(getCustomName());
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setCustomNameVisible(true), 1);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setGravity(false), 3);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setInvulnerable(true), 5);
        entity.setItemStack(getDisplay());
        initAnimations(plugin);
    }

    public void setDisplay(ItemStack display) {
        this.display = display;
    }
}
