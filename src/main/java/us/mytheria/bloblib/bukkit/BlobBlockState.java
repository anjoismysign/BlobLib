package us.mytheria.bloblib.bukkit;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccess;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlockState;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * A small utility class to allow updating a block state in a specific location
 */
public class BlobBlockState {
    private final CraftBlockState blockState;
    private final static Field positionField;

    static {
        try {
            positionField = CraftBlockState.class.getDeclaredField("position");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final static Field worldField;

    static {
        try {
            worldField = CraftBlockState.class.getDeclaredField("world");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static BlobBlockState of(BlockState blockState) {
        return new BlobBlockState((CraftBlockState) blockState);
    }

    private BlobBlockState(CraftBlockState blockState) {
        this.blockState = blockState;
    }

    /**
     * Gets the implementation of BlockState
     *
     * @return the implementation of BlockState
     */
    public CraftBlockState getImplementation() {
        return blockState;
    }

    /**
     * Allows updating a block state in a specific location
     * without changing the original block state's location.
     *
     * @param force        true to forcefully set the state
     * @param applyPhysics false to cancel updating physics on surrounding blocks
     * @param location     the location to update the block state at
     * @return true if the update was successful, false otherwise
     */
    public boolean update(boolean force, boolean applyPhysics, @NotNull Location location) {
        CraftWorld world = (CraftWorld) Objects.requireNonNull(location.getWorld());
        GeneratorAccess oldAccess = world.getHandle();
        blockState.setWorldHandle(world.getHandle());
        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        positionField.setAccessible(true);
        worldField.setAccessible(true);
        BlockPosition oldPosition;
        CraftWorld oldWorld;
        boolean update = false;
        try {
            oldPosition = (BlockPosition) positionField.get(blockState);
            oldWorld = (CraftWorld) worldField.get(blockState);
            positionField.set(blockState, blockPosition);
            worldField.set(blockState, world);
            update = blockState.update(force, applyPhysics);
            worldField.set(blockState, oldWorld);
            positionField.set(blockState, oldPosition);
        } catch (IllegalAccessException ignored) {
            Bukkit.getLogger().severe("Failed to update block state");
        }
        positionField.setAccessible(false);
        blockState.setWorldHandle(oldAccess);
        return update;
    }
}
