package io.github.anjoismysign.bloblib.utilities;

import io.github.anjoismysign.anjo.entities.Uber;
import io.github.anjoismysign.bloblib.entities.ChainedTask;
import io.github.anjoismysign.bloblib.entities.ChainedTaskProgress;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.structure.Palette;
import org.bukkit.structure.Structure;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Your decorator guy for structure related stuff.
 * Includes I/O, fill and placement.
 * Needs further testing
 */
public class Structrador {
    protected final Structure structure;
    protected final JavaPlugin plugin;

    /**
     * Will create a structure from the given location and radius.
     * Let's say that the center is at 0,0,0 and the radius is 5,5,5.
     * The structure will cover from -5,-5,-5 to 5,5,5.
     *
     * @param plugin          - The plugin that will be used to create the structure.
     * @param center          - The center of the structure.
     * @param radius          - The radius of the structure.
     * @param includeEntities - If the entities present in the structure should be included.
     * @return A Structrador with the structure.
     */
    @NotNull
    public static Structrador CENTERED(JavaPlugin plugin, Location center,
                                       Vector radius, boolean includeEntities) {
        Structure structure = Bukkit.getStructureManager().createStructure();
        structure.fill(Objects.requireNonNull(center).clone().subtract(Objects.requireNonNull(radius)),
                center.clone().add(radius), includeEntities);
        return new Structrador(structure, plugin);
    }

    /**
     * Will create a structure from the given locations.
     * The structure will cover from the first corner to the second corner.
     *
     * @param plugin          - The plugin that will be used to create the structure.
     * @param pos1            - The first corner of the structure.
     * @param pos2            - The second corner of the structure.
     * @param includeEntities - If the entities present in the structure should be included.
     * @return A Structrador with the structure.
     */
    @NotNull
    public static Structrador CORNERS(JavaPlugin plugin, Location pos1,
                                      Location pos2, boolean includeEntities) {
        Structure structure = Bukkit.getStructureManager().createStructure();
        if (!Objects.equals(Objects.requireNonNull(pos1).getWorld(), Objects.requireNonNull(pos2).getWorld()))
            throw new IllegalArgumentException("Locations must be in the same world.");
        Location min = new Location(pos1.getWorld(), Math.min(pos1.getX(), pos2.getX()),
                Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
        Location max = new Location(pos1.getWorld(), Math.max(pos1.getX(), pos2.getX()),
                Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
        structure.fill(min, max, includeEntities);
        return new Structrador(structure, plugin);
    }

    public Structrador(Structure structure, JavaPlugin plugin) {
        this.structure = Objects.requireNonNull(structure);
        this.plugin = Objects.requireNonNull(plugin);
    }

    public Structrador(File file, JavaPlugin plugin) {
        Structure structure;
        try {
            structure = Bukkit.getStructureManager().loadStructure(file);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.structure = structure;
        this.plugin = Objects.requireNonNull(plugin);
    }

    public Structrador(InputStream inputStream, JavaPlugin plugin) {
        Structure structure;
        try {
            structure = Bukkit.getStructureManager().loadStructure(inputStream);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.structure = structure;
        this.plugin = Objects.requireNonNull(plugin);
    }

    public Structrador(byte[] bytes, JavaPlugin plugin) {
        this(new ByteArrayInputStream(bytes), plugin);
    }

    public Structrador(Blob blob, JavaPlugin plugin) {
        this(BlobUtil.blobToInputStream(blob), plugin);
    }

    public Structure getStructure() {
        return structure;
    }

    public ByteArrayOutputStream toOutputStream() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Bukkit.getStructureManager().saveStructure(outputStream, structure);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return outputStream;
    }

    public byte[] toByteArray() {
        return toOutputStream().toByteArray();
    }

    public Blob toBlob() {
        return BlobUtil.byteArrayOutputStreamToBlob(toOutputStream());
    }

    /**
     * Will place the structure at the given location in the same tick.
     *
     * @param location          - The location to place the structure at.
     * @param includeEntities   - If the entities present in the structure should be spawned.
     * @param structureRotation - The rotation of the structure.
     * @param mirror            - The mirror settings of the structure.
     * @param palette           - The palette index of the structure to use, starting at 0, or -1 to pick a random palette.
     * @param integrity         - Determines how damaged the building should look by randomly skipping blocks to place. This value can range from 0 to 1. With 0 removing all blocks and 1 spawning the structure in pristine condition.
     * @param random            - The randomizer used for setting the structure's LootTables and integrity.
     */
    public void simultaneousPlace(Location location, boolean includeEntities,
                                  StructureRotation structureRotation, Mirror mirror, int palette,
                                  float integrity, Random random) {
        structure.place(location, includeEntities,
                structureRotation, mirror, palette, integrity, random);
    }

    /**
     * Will place the structure at the given location in the same tick.
     * A consumer will be called for each block placed.
     * NOTE: AIR blocks are not ignored!
     *
     * @param location          - The location to place the structure at.
     * @param includeEntities   - If the entities present in the structure should be spawned.
     * @param structureRotation - The rotation of the structure.
     * @param mirror            - The mirror settings of the structure.
     * @param palette           - The palette index of the structure to use, starting at 0, or -1 to pick a random palette.
     * @param integrity         - Determines how damaged the building should look by randomly skipping blocks to place. This value can range from 0 to 1. With 0 removing all blocks and 1 spawning the structure in pristine condition.
     * @param random            - The randomizer used for setting the structure's LootTables and integrity.
     * @param consumer          - The consumer that will be called when a block is placed.
     */
    public void simultaneousPlace(Location location, boolean includeEntities,
                                  StructureRotation structureRotation, Mirror mirror, int palette,
                                  float integrity, Random random,
                                  Consumer<Block> consumer) {
        simultaneousPlace(location, includeEntities, structureRotation, mirror, palette, integrity, random);
        structure.getPalettes().stream().map(Palette::getBlocks).flatMap(List::stream).forEach(state -> {
            int x = state.getX();
            int y = state.getY();
            int z = state.getZ();
            Block block = location.clone().add(Vectorator.of(x, y, z).rotate(structureRotation)).getBlock();
            consumer.accept(block);
        });
    }

    /**
     * Will place the structure at the given location in multiple ticks.
     * The bigger the structure, the longer it will take to place.
     * TODO: mirror, palette, integrity and random are not used.
     *
     * @param location             - The location to place the structure at.
     * @param includeEntities      - If the entities present in the structure should be spawned.
     * @param structureRotation    - The rotation of the structure.
     * @param mirror               - The mirror settings of the structure.
     * @param palette              - The palette index of the structure to use, starting at 0, or -1 to pick a random palette.
     * @param integrity            - Determines how damaged the building should look by randomly skipping blocks to place. This value can range from 0 to 1. With 0 removing all blocks and 1 spawning the structure in pristine condition.
     * @param random               - The randomizer used for setting the structure's LootTables and integrity.
     * @param maxPlacedPerPeriod   - The maximum amount of blocks/entities placed per period.
     * @param period               - The period between each placement in ticks.
     * @param placedBlockConsumer  - The consumer that will be called when a block is placed.
     * @param placedEntityConsumer - The consumer that will be called when an entity is placed.
     * @return A CompletableFuture that completes when the structure is placed.
     */
    @NotNull
    public ChainedTask chainedPlace(Location location, boolean includeEntities,
                                    StructureRotation structureRotation, Mirror mirror,
                                    int palette, float integrity, Random random,
                                    int maxPlacedPerPeriod, long period,
                                    Consumer<BlockState> placedBlockConsumer,
                                    Consumer<Entity> placedEntityConsumer) {
        /*
         * The location of the returned block states and entities
         * are offsets relative to the structure's position that
         * is provided once the structure is placed into the world.
         */
        CompletableFuture<Void> future = new CompletableFuture<>();
        List<Palette> palettes = structure.getPalettes();
        List<BlockState> blocks = palettes.stream().map(Palette::getBlocks)
                .flatMap(List::stream).toList();
        List<Entity> entities = structure.getEntities();
        int blocksSize = blocks.size();
        int entitiesSize = entities.size();
        int totalSize = blocksSize + entitiesSize;
        ChainedTask chainedTask = new ChainedTask(future, null,
                (totalSize / maxPlacedPerPeriod) * period);
        ChainedTaskProgress progress = new ChainedTaskProgress(totalSize, chainedTask);
        Iterator<BlockState> blockIterator = blocks.iterator();
        World world = location.getWorld();
        if (world == null)
            throw new IllegalArgumentException("Location must have a world.");
        int degree;
        Vector blockOffset;
        Vector entityOffset;
        BlockVector size = structure.getSize();
        float extraYaw;
        switch (structureRotation) {
            case NONE -> {
                degree = 0;
                blockOffset = new Vector(0, 0, 0);
                entityOffset = new Vector(0, 0, 0);
                extraYaw = 0;
            }
            case CLOCKWISE_90 -> {
                degree = 270;
                blockOffset = new Vector(size.getX() - 1, 0, 0);
                entityOffset = new Vector(size.getX(), 0, 0);
                extraYaw = 90;
            }
            case CLOCKWISE_180 -> {
                degree = 180;
                blockOffset = new Vector(size.getX() - 1, 0, size.getZ() - 1);
                entityOffset = new Vector(size.getX(), 0, size.getZ());
                extraYaw = 180;
            }
            case COUNTERCLOCKWISE_90 -> {
                degree = 90;
                blockOffset = new Vector(0, 0, size.getZ() - 1);
                entityOffset = new Vector(0, 0, size.getZ());
                extraYaw = 270;
            }
            default -> {
                throw new IllegalStateException("Unexpected value: " + structureRotation);
            }
        }
        try {
            // Will place blocks
            CompletableFuture<Void> paletteFuture = new CompletableFuture<>();
            BukkitRunnable placeTask = new BukkitRunnable() {
                @Override
                public void run() {
                    Uber<Integer> placed = Uber.drive(0);
                    while (blockIterator.hasNext() && placed.thanks() < maxPlacedPerPeriod) {
                        BlockState next = blockIterator.next();
                        Vector nextVector = next.getLocation().toVector();
                        Vector result = VectorUtil.rotateVector(nextVector, degree);
                        Location blockLocation = location.clone()
                                .add(result.getX() + blockOffset.getX(),
                                        result.getY() + blockOffset.getY(),
                                        result.getZ() + blockOffset.getZ());
                        BlockState state = next.copy(blockLocation);
                        state.update(true, false);
                        BlockState current = blockLocation.getBlock().getState();
                        BlockData data = current.getBlockData();
                        if (data instanceof Directional directional) {
                            directional.setFacing(BlockFaceUtil
                                    .rotateCardinalDirection(directional.getFacing(),
                                            structureRotation));
                            current.setBlockData(directional);
                            current.update(true, false);
                        }
                        placedBlockConsumer.accept(current);
                        placed.talk(placed.thanks() + 1);
                        progress.run();
                    }
                    if (!blockIterator.hasNext()) {
                        paletteFuture.complete(null);
                        this.cancel();
                    }
                }
            };
            chainedTask.setTask(placeTask.runTaskTimer(plugin, 1L, period));
            //Once blocks are placed, will place entities
            paletteFuture.whenComplete((aVoid, throwable) -> {
                if (!includeEntities) {
                    future.complete(null);
                    return;
                }
                Iterator<Entity> entityIterator = entities.iterator();
                BukkitRunnable entityTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Uber<Integer> placed = Uber.drive(0);
                        while (entityIterator.hasNext() && placed.thanks() < maxPlacedPerPeriod) {
                            Entity next = entityIterator.next();
                            next.copy();
                            Location nextLocation = next.getLocation();
                            Vector nextVector = nextLocation.toVector();
                            Vector result = VectorUtil.floatRotateVector(nextVector, degree);
                            Location entityLocation = location.clone()
                                    .add(result.getX() + entityOffset.getX(),
                                            result.getY() + entityOffset.getY(),
                                            result.getZ() + entityOffset.getZ());
                            entityLocation.setYaw(nextLocation.getYaw() + extraYaw);
                            boolean isSilent = next.isSilent();
                            next.setSilent(true);
                            Entity added = next.copy(entityLocation);
                            if (added instanceof Hanging hanging) {
                                BlockFace facing = hanging.getFacing();
                                hanging.setFacingDirection(
                                        BlockFaceUtil.rotateCardinalDirection(facing, structureRotation),
                                        true);
                            }
                            added.setSilent(isSilent);
                            placedEntityConsumer.accept(added);
                            placed.talk(placed.thanks() + 1);
                            progress.run();
                        }
                        if (!entityIterator.hasNext()) {
                            future.complete(null);
                            this.cancel();
                        }
                    }
                };
                chainedTask.setTask(entityTask.runTaskTimer(plugin, 1L, period));
            });
        } catch (Throwable throwable) {
            future.completeExceptionally(throwable);
            throwable.printStackTrace();
        }
        return chainedTask;
    }

    /**
     * Will place the structure at the given location in multiple ticks.
     * The bigger the structure, the longer it will take to place.
     * Places 1 block/entity per tick.
     * TODO: mirror, palette, integrity and random are not used.
     *
     * @param location          - The location to place the structure at.
     * @param includeEntities   - If the entities present in the structure should be spawned.
     * @param structureRotation - The rotation of the structure.
     * @param mirror            - The mirror settings of the structure.
     * @param palette           - The palette index of the structure to use, starting at 0, or -1 to pick a random palette.
     * @param integrity         - Determines how damaged the building should look by randomly skipping blocks to place. This value can range from 0 to 1. With 0 removing all blocks and 1 spawning the structure in pristine condition.
     * @param random            - The randomizer used for setting the structure's LootTables and integrity.
     * @return A CompletableFuture that completes when the structure is placed.
     */
    public ChainedTask chainedPlace(Location location, boolean includeEntities,
                                    StructureRotation structureRotation, Mirror mirror,
                                    int palette, float integrity, Random random) {
        return chainedPlace(location, includeEntities, structureRotation, mirror, palette,
                integrity, random, 1, 1, blockState -> {
                }, entity -> {
                });
    }
}