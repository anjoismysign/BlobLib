package us.mytheria.bloblib.displayentity;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
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
    public ItemDisplayFloatingPet(Player owner, ItemStack display,
                                  @Nullable Particle particle,
                                  @Nullable String customName,
                                  EntityAnimationsCarrier animationsCarrier,
                                  double particlesOffset) {
        super(owner, display, particle, customName, animationsCarrier, particlesOffset);
    }

    void spawnEntity(Location location) {
        BlobLib plugin = BlobLib.getInstance();
        entity = (ItemDisplay) location.getWorld().spawnEntity(location,
                EntityType.ITEM_DISPLAY);
        vehicle = (ItemDisplay) location.getWorld().spawnEntity(location,
                EntityType.ITEM_DISPLAY);
        entity.setPersistent(false);
        vehicle.setPersistent(false);
        if (!vehicle.addPassenger(entity)) {
            throw new RuntimeException("Failed to add passenger to vehicle");
        }
        setCustomName(getCustomName());
        entity.setItemStack(getDisplay());
        Transformation transformation = entity.getTransformation();
        entity.setTransformation(new Transformation(
                transformation.getTranslation().add(0f, -0.5f, 0f),
                transformation.getLeftRotation(), transformation.getScale(),
                transformation.getRightRotation()));
        initAnimations(plugin);
    }

    public void setDisplay(ItemStack display) {
        this.display = display;
    }
}
