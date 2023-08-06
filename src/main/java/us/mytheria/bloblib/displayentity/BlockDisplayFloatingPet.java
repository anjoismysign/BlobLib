package us.mytheria.bloblib.displayentity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;

public class BlockDisplayFloatingPet extends DisplayFloatingPet<BlockDisplay, BlockData> {
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
    public BlockDisplayFloatingPet(Player owner, BlockData display, @Nullable Particle particle, @Nullable String customName) {
        super(owner, display, particle, customName);
    }

    void spawnEntity(Location location) {
        BlobLib plugin = BlobLib.getInstance();
        entity = (BlockDisplay) location.getWorld().spawnEntity(location,
                EntityType.BLOCK_DISPLAY);
        setCustomName(getCustomName());
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setCustomNameVisible(true), 1);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setGravity(false), 3);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> entity.setInvulnerable(true), 5);
        entity.setBlock(getDisplay());
        initAnimations(plugin);
    }

    public void setDisplay(BlockData display) {
        this.display = display;
    }
}
