package us.mytheria.bloblib.displayentity;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.display.DisplayDecorator;

public class ItemDisplayPackFloatingPet extends DisplayPackFloatingPet<ItemDisplay, ItemStack> {
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
    public ItemDisplayPackFloatingPet(Player owner,
                                      ItemStack display,
                                      @Nullable Particle particle,
                                      @Nullable String customName,
                                      DisplayFloatingPetSettings settings,
                                      @NotNull PackMaster packMaster,
                                      int index) {
        super(owner, display, particle, customName, settings, packMaster, index);
    }

    void spawnEntity(Location location) {
        BlobLib plugin = BlobLib.getInstance();
        entity = (ItemDisplay) location.getWorld().spawnEntity(location,
                EntityType.ITEM_DISPLAY);
        entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
        entity.setPersistent(false);
        entity.setTeleportDuration(1);
        setCustomName(getCustomName());
        entity.setItemStack(getDisplay());
        Transformation transformation = entity.getTransformation();
        entity.setTransformation(new Transformation(
                transformation.getTranslation().add(0f, -0.5f, 0f),
                transformation.getLeftRotation(), transformation.getScale(),
                transformation.getRightRotation()));
        DisplayDecorator<ItemDisplay> decorator = new DisplayDecorator<>(entity, plugin);
        decorator.transformLeft(1, 0, 0, 90, 1);
        initAnimations(plugin);
    }

    public void setDisplay(ItemStack display) {
        this.display = display;
    }
}
