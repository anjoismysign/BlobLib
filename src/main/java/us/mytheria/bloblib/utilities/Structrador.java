package us.mytheria.bloblib.utilities;

import me.anjoismysign.anjo.entities.Uber;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.structure.Palette;
import org.bukkit.structure.Structure;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.*;
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
    private final Structure structure;
    private final JavaPlugin plugin;

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.structure = structure;
        this.plugin = Objects.requireNonNull(plugin);
    }

    public Structrador(InputStream inputStream, JavaPlugin plugin) {
        Structure structure;
        try {
            structure = Bukkit.getStructureManager().loadStructure(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        structure.place(location, includeEntities, structureRotation, mirror, palette, integrity, random);
    }

    /**
     * Will place the structure at the given location in multiple ticks.
     * The bigger the structure, the longer it will take to place.
     * TODO: structureRotation, mirror, palette, integrity and random are not used.
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
    public CompletableFuture<Void> chainedPlace(Location location, boolean includeEntities,
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
        CompletableFuture<Void> chainedPlace = new CompletableFuture<>();
        List<Palette> palettes = structure.getPalettes();
        List<BlockState> blocks = palettes.stream().map(Palette::getBlocks)
                .flatMap(List::stream).toList();
        Iterator<BlockState> iterator = blocks.iterator();
        World world = location.getWorld();
        if (world == null)
            throw new IllegalArgumentException("Location must have a world.");
        try {
            // Will place blocks
            CompletableFuture<Void> paletteFuture = new CompletableFuture<>();
            BukkitRunnable placeTask = new BukkitRunnable() {
                @Override
                public void run() {
                    Uber<Integer> maxPlaced = Uber.drive(0);
                    while (iterator.hasNext() && maxPlaced.thanks() < maxPlacedPerPeriod) {
                        BlockState blockState = iterator.next();
                        Location blockLocation = location.clone().add(blockState.getX(), blockState.getY(), blockState.getZ());
                        BlockState current = blockLocation.getBlock().getState();
                        current.setType(blockState.getType());
                        current.setBlockData(blockState.getBlockData());
                        current.update(true, false);
                        placedBlockConsumer.accept(current);
                        maxPlaced.talk(maxPlaced.thanks() + 1);
                    }
                    if (!iterator.hasNext()) {
                        paletteFuture.complete(null);
                        this.cancel();
                    }
                }
            };
            placeTask.runTaskTimer(plugin, 1L, period);
            //Once blocks are placed, will place entities
            paletteFuture.whenComplete((aVoid, throwable) -> {
                if (!includeEntities) {
                    chainedPlace.complete(null);
                    return;
                }
                Iterator<Entity> entityIterator = structure.getEntities().iterator();
                BukkitRunnable entityTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Uber<Integer> maxPlaced = Uber.drive(0);
                        while (entityIterator.hasNext() && maxPlaced.thanks() < maxPlacedPerPeriod) {
                            Entity entity = entityIterator.next();
                            Location offset = entity.getLocation();
                            Location entityLocation = location.clone().add(offset.getX(), offset.getY(), offset.getZ());
                            entityLocation.setPitch(offset.getPitch());
                            entityLocation.setYaw(offset.getYaw());
                            placedEntityConsumer.accept(entity);
                            /*
                             * Intermediate level of API-NMS
                             * Known to work in 1.20.1
                             * Might break in future versions
                             */
                            var nmsWorld = ((CraftWorld) world).getHandle();
                            var nmsEntity = ((CraftEntity) entity).getHandle();
                            if (!nmsWorld.tryAddFreshEntityWithPassengers
                                    (nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM))
                                continue;
                            entity = nmsEntity.getBukkitEntity();
                            entity.teleport(entityLocation);
                            maxPlaced.talk(maxPlaced.thanks() + 1);
                        }
                        if (!iterator.hasNext()) {
                            chainedPlace.complete(null);
                            this.cancel();
                        }
                    }
                };
                entityTask.runTaskTimer(plugin, 1L, period);
            });
        } catch (Exception e) {
            chainedPlace.completeExceptionally(e);
        }
        return chainedPlace;
    }

    /**
     * Will place the structure at the given location in multiple ticks.
     * The bigger the structure, the longer it will take to place.
     * Places 1 block/entity per tick.
     * TODO: structureRotation, mirror, palette, integrity and random are not used.
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
    public CompletableFuture<Void> chainedPlace(Location location, boolean includeEntities,
                                                StructureRotation structureRotation, Mirror mirror,
                                                int palette, float integrity, Random random) {
        return chainedPlace(location, includeEntities, structureRotation, mirror, palette,
                integrity, random, 1, 1, blockState -> {
                }, entity -> {
                });
    }
}