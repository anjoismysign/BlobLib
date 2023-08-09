package us.mytheria.bloblib.displayentity;

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
    public BlockDisplayFloatingPet(Player owner, BlockData display,
                                   @Nullable Particle particle,
                                   @Nullable String customName,
                                   DisplayFloatingPetSettings settings) {
        super(owner, display, particle, customName, settings);
    }

    void spawnEntity(Location location) {
        BlobLib plugin = BlobLib.getInstance();
        entity = (BlockDisplay) location.getWorld().spawnEntity(location,
                EntityType.BLOCK_DISPLAY);
        vehicle = (BlockDisplay) location.getWorld().spawnEntity(location,
                EntityType.BLOCK_DISPLAY);
        entity.setPersistent(false);
        vehicle.setPersistent(false);
        if (!vehicle.addPassenger(entity)) {
            throw new RuntimeException("Failed to add passenger to vehicle");
        }
        setCustomName(getCustomName());
        entity.setBlock(getDisplay());
        initAnimations(plugin);
    }

    public void setDisplay(BlockData display) {
        this.display = display;
    }
}
