package us.mytheria.bloblib.entities.display;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record DisplayBuilder(@Nullable ItemStack itemStack,
                             @Nullable BlockData blockData,
                             Vector3f scale,
                             Quaternionf leftRotation,
                             Quaternionf rightRotation,
                             Vector3f translation) {
    /**
     * Creates a new DisplayBuilder with the given BlockData.
     *
     * @param blockData the BlockData
     * @return the DisplayBuilder
     */
    public static DisplayBuilder of(BlockData blockData) {
        if (blockData == null)
            throw new IllegalArgumentException("BlockData cannot be null!");
        return new DisplayBuilder(null,
                null,
                new Vector3f(1, 1, 1),
                new Quaternionf(0, 0, 0, 1),
                new Quaternionf(0, 0, 0, 1),
                new Vector3f(0.5f, 0, 0.5f));
    }

    /**
     * Creates a new DisplayBuilder with the given Material.
     *
     * @param material the Material
     * @return the DisplayBuilder
     */
    public static DisplayBuilder of(Material material) {
        return of(new ItemStack(material));
    }

    /**
     * Creates a new DisplayBuilder with the given ItemStack.
     *
     * @param itemStack the ItemStack
     * @return the DisplayBuilder
     */
    public static DisplayBuilder of(ItemStack itemStack) {
        if (itemStack == null)
            throw new IllegalArgumentException("ItemStack cannot be null!");
        if (itemStack.getType() == Material.AIR)
            throw new IllegalArgumentException("ItemStack cannot be AIR!");
        return new DisplayBuilder(itemStack,
                null,
                new Vector3f(1, 1, 1),
                new Quaternionf(0, 0, 0, 1),
                new Quaternionf(0, 0, 0, 1),
                new Vector3f(0.5f, 0, 0.5f));
    }

    /**
     * Updates the ItemStack
     *
     * @param display the new ItemStack
     * @return the DisplayBuilder
     */
    public DisplayBuilder setItemStack(ItemStack display) {
        if (display == null)
            throw new IllegalArgumentException("ItemStack cannot be null!");
        if (display.getType() == Material.AIR)
            throw new IllegalArgumentException("ItemStack cannot be AIR!");
        return new DisplayBuilder(display, blockData, scale, leftRotation, rightRotation, translation);
    }

    /**
     * Updates the BlockData
     *
     * @param display the new BlockData
     * @return the DisplayBuilder
     */
    public DisplayBuilder setBlockData(BlockData display) {
        if (display == null)
            throw new IllegalArgumentException("BlockData cannot be null!");
        return new DisplayBuilder(itemStack, display, scale, leftRotation, rightRotation, translation);
    }

    /**
     * Will uniformily scale the display.
     * It's the same as calling {@link #scale(float, float, float)} with the same value for x, y and z.
     *
     * @param scale the scale (note that '1.0f' as the reference for the original
     *              block/falling block size)
     * @return the DisplayBuilder
     */
    public DisplayBuilder uniformScale(float scale) {
        return new DisplayBuilder(itemStack, blockData,
                new Vector3f(scale, scale, scale),
                leftRotation, rightRotation, translation);
    }

    /**
     * Will scale the display.
     * (note that '1.0f' as the reference for the original
     * block/falling block size)
     *
     * @param x the x scale
     * @param y the y scale
     * @param z the z scale
     * @return the DisplayBuilder
     */
    public DisplayBuilder scale(float x, float y, float z) {
        return new DisplayBuilder(itemStack, blockData,
                new Vector3f(x, y, z),
                leftRotation, rightRotation, translation);
    }

    /**
     * Will translate/move the display.
     * (halving the scale will center the display)
     *
     * @param x the x translation
     * @param y the y translation
     * @param z the z translation
     * @return the DisplayBuilder
     */
    public DisplayBuilder translate(float x, float y, float z) {
        return new DisplayBuilder(itemStack, blockData, scale,
                leftRotation, rightRotation, new Vector3f(x, y, z));
    }

    /**
     * Will translate/move the display vertically / on the y-axis.
     * Will keep previous x and z translation values.
     *
     * @param y the y translation
     * @return the DisplayBuilder
     */
    public DisplayBuilder translateVertical(float y) {
        return new DisplayBuilder(itemStack, blockData, scale,
                leftRotation, rightRotation,
                new Vector3f(translation.x, y, translation.z));
    }

    /**
     * Will translate/move the display horizontally / on the x and z axis.
     * In other words, will center the display while staying in the same
     * Y coordinate in game.
     *
     * @return the DisplayBuilder
     */
    public DisplayBuilder centerHorizontal() {
        return new DisplayBuilder(itemStack, blockData, scale,
                leftRotation, rightRotation,
                new Vector3f(scale.x / 2, 0, scale.z / 2));
    }

    /**
     * Will center the display, ignoring the y-axis.
     *
     * @return the DisplayBuilder
     */
    public DisplayBuilder center() {
        return new DisplayBuilder(itemStack, blockData, scale,
                leftRotation, rightRotation,
                new Vector3f(scale.x / 2, scale.y / 2, scale.z / 2));
    }

    /**
     * Will manipulate the leftRotation while keeping a 1:1 (1.0f) scale.
     *
     * @param x the x rotation
     * @param y the y rotation
     * @param z the z rotation
     * @return the DisplayBuilder
     */
    public DisplayBuilder rotateLeft(float x, float y, float z) {
        return rotateLeft(x, y, z, 1);
    }

    /**
     * Will manipulate the leftRotation
     *
     * @param x      the x rotation
     * @param y      the y rotation
     * @param z      the z rotation
     * @param scalar the scalar
     * @return the DisplayBuilder
     */
    public DisplayBuilder rotateLeft(float x, float y, float z, float scalar) {
        return new DisplayBuilder(itemStack, blockData, this.scale,
                new Quaternionf(x, y, z, scalar), rightRotation, translation);
    }

    /**
     * Will manipulate the rightRotation while keeping a 1:1 (1.0f) scale.
     *
     * @param x the x rotation
     * @param y the y rotation
     * @param z the z rotation
     * @return the DisplayBuilder
     */
    public DisplayBuilder rotateRight(float x, float y, float z) {
        return rotateRight(x, y, z, 1);
    }

    /**
     * Will manipulate the rightRotation
     *
     * @param x      the x rotation
     * @param y      the y rotation
     * @param z      the z rotation
     * @param scalar the scalar
     * @return the DisplayBuilder
     */
    public DisplayBuilder rotateRight(float x, float y, float z, float scalar) {
        return new DisplayBuilder(itemStack, blockData, this.scale,
                leftRotation, new Quaternionf(x, y, z, scalar), translation);
    }

    private Transformation transformation() {
        return new Transformation(scale, leftRotation, scale, rightRotation);
    }

    /**
     * Will spawn an ItemDisplay at the given location
     *
     * @param location the spawn location
     * @return the ItemDisplay
     */
    public ItemDisplay toItemDisplay(Location location) {
        if (itemStack == null)
            throw new IllegalArgumentException("ItemStack cannot be null!");
        if (itemStack.getType() == Material.AIR)
            throw new IllegalArgumentException("ItemStack cannot be AIR!");
        
        Entity entity = location.getWorld().spawnEntity(location,
                EntityType.ITEM_DISPLAY);
        ItemDisplay itemDisplay = (ItemDisplay) entity;
        itemDisplay.setItemStack(itemStack);
        itemDisplay.setTransformation(transformation());
        return itemDisplay;
    }

    /**
     * Will spawn an ItemDisplay at the given location
     * and return a DisplayDecorator for it instead
     * of the ItemDisplay itself.
     *
     * @param location the spawn location
     * @return the DisplayDecorator
     */
    public DisplayDecorator<ItemDisplay> toItemDisplayDecorator(Location location) {
        return new DisplayDecorator<>(toItemDisplay(location));
    }

    /**
     * Will spawn a BlockDisplay at the given location
     * using the cached BlockData.
     *
     * @param location the spawn location
     * @return the BlockDisplay
     */
    public BlockDisplay toBlockDisplay(Location location) {
        return toBlockDisplay(location, null);
    }

    /**
     * Will spawn a BlockDisplay at the given location
     * providing a fallback BlockData in case the
     * cached BlockData is null.
     *
     * @param location the spawn location
     * @return the BlockDisplay
     */
    public BlockDisplay toBlockDisplay(Location location, @Nullable BlockData display) {
        BlockData data = this.blockData == null ? display : this.blockData;
        if (data == null)
            throw new IllegalArgumentException("BlockData not set");
        Entity entity = location.getWorld().spawnEntity(location,
                EntityType.BLOCK_DISPLAY);
        BlockDisplay blockDisplay = (BlockDisplay) entity;
        blockDisplay.setBlock(data);
        blockDisplay.setTransformation(transformation());
        return blockDisplay;
    }

    /**
     * Will spawn a BlockDisplay at the given location
     * using the cached BlockData.
     * Will return a DisplayDecorator for it instead
     * of the BlockDisplay itself.
     *
     * @param location the spawn location
     * @return the DisplayDecorator
     */
    public DisplayDecorator<BlockDisplay> toBlockDisplayDecorator(Location location) {
        return toBlockDisplayDecorator(location, null);
    }

    /**
     * Will spawn a BlockDisplay at the given location
     * providing a fallback BlockData in case the
     * cached BlockData is null.
     * Will return a DisplayDecorator for it instead
     * of the BlockDisplay itself.
     *
     * @param location the spawn location
     * @return the DisplayDecorator
     */
    public DisplayDecorator<BlockDisplay> toBlockDisplayDecorator(Location location,
                                                                  BlockData display) {
        return new DisplayDecorator<>(toBlockDisplay(location, display));
    }
}
