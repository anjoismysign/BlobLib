package us.mytheria.bloblib.entities;

import me.anjoismysign.anjo.entities.Uber;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Cuboid {

    private final int xMin;
    private final int xMax;
    private final int yMin;
    private final int yMax;
    private final int zMin;
    private final int zMax;
    private final double xMinCentered;
    private final double xMaxCentered;
    private final double yMinCentered;
    private final double yMaxCentered;
    private final double zMinCentered;
    private final double zMaxCentered;
    private final World world;

    public static Cuboid of(@NotNull Vector min,
                            @NotNull Vector max,
                            @NotNull World world) {
        Location point1 = Objects.requireNonNull(min)
                .toLocation(Objects.requireNonNull(world));
        Location point2 = Objects.requireNonNull(max).toLocation(world);
        return new Cuboid(point1, point2);
    }

    public Cuboid(final Location point1, final Location point2) {
        this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        this.yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        this.yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        this.world = point1.getWorld();
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
    }

    public BoundingBox toBoundingBox() {
        return new BoundingBox(this.xMin, this.yMin, this.zMin, this.xMax, this.yMax, this.zMax);
    }

    /**
     * Will process all blocks in the cuboid and apply the consumer to them.
     *
     * @param consumer The consumer to apply to the blocks.
     */
    public void forEachBlock(Consumer<Block> consumer) {
        for (int x = this.xMin; x <= this.xMax; ++x) {
            for (int y = this.yMin; y <= this.yMax; ++y) {
                for (int z = this.zMin; z <= this.zMax; ++z) {
                    final Block blockAt = this.world.getBlockAt(x, y, z);
                    consumer.accept(blockAt);
                }
            }
        }
    }

    /**
     * Should chain the forEachBlock method to allow for a max per period.
     *
     * @param consumer     The consumer to apply to the blocks.
     * @param maxPerPeriod The max amount of blocks to process per period.
     * @param period       The period in ticks to process the blocks.
     * @param plugin       The plugin to run the task on.
     * @return A completable future that will complete when the task is done.
     */
    public ChainedTask chainedForEach(Consumer<Block> consumer,
                                      int maxPerPeriod,
                                      long period,
                                      @NotNull JavaPlugin plugin) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        int size = getTotalBlockSize();
        long estimatedTime = (size / maxPerPeriod) * period;
        ChainedTask chainedTask = new ChainedTask(future, null,
                estimatedTime);
        ChainedTaskProgress progress = new ChainedTaskProgress(size, chainedTask);
        Iterator<Block> iterator = blockList().iterator();
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                Uber<Integer> placed = Uber.drive(0);
                while (iterator.hasNext() && placed.thanks() < maxPerPeriod) {
                    Block next = iterator.next();
                    consumer.accept(next);
                    placed.talk(placed.thanks() + 1);
                    progress.run();
                }
                if (!iterator.hasNext()) {
                    future.complete(null);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, period);
        chainedTask.setTask(task);
        return chainedTask;
    }

    /**
     * Get a list of the blocks contained within this cuboid.
     *
     * @return A list of Block objects
     */
    public ArrayList<Block> blockList() {
        final ArrayList<Block> list = new ArrayList<>(this.getTotalBlockSize());
        forEachBlock(list::add);
        return list;
    }

    public Location getCenter() {
        return new Location(this.world, (this.xMax - this.xMin) / 2 + this.xMin,
                (this.yMax - this.yMin) / 2 + this.yMin, (this.zMax - this.zMin) / 2 + this.zMin);
    }

    public double getDistance() {
        return this.getPoint1().distance(this.getPoint2());
    }

    public double getDistanceSquared() {
        return this.getPoint1().distanceSquared(this.getPoint2());
    }

    public int getHeight() {
        return this.yMax - this.yMin + 1;
    }

    public Location getPoint1() {
        return new Location(this.world, this.xMin, this.yMin, this.zMin);
    }

    public Location getPoint2() {
        return new Location(this.world, this.xMax, this.yMax, this.zMax);
    }

    public Location getRandomLocation() {
        final Random rand = new Random();
        final int x = rand.nextInt(Math.abs(this.xMax - this.xMin) + 1) + this.xMin;
        final int y = rand.nextInt(Math.abs(this.yMax - this.yMin) + 1) + this.yMin;
        final int z = rand.nextInt(Math.abs(this.zMax - this.zMin) + 1) + this.zMin;
        return new Location(this.world, x, y, z);
    }

    public int getTotalBlockSize() {
        return this.getHeight() * this.getXWidth() * this.getZWidth();
    }

    public int getXWidth() {
        return this.xMax - this.xMin + 1;
    }

    public int getZWidth() {
        return this.zMax - this.zMin + 1;
    }

    public boolean isIn(final Location loc) {
        return loc.getWorld() == this.world && loc.getBlockX() >= this.xMin && loc.getBlockX() <= this.xMax
                && loc.getBlockY() >= this.yMin && loc.getBlockY() <= this.yMax && loc.getBlockZ() >= this.zMin
                && loc.getBlockZ() <= this.zMax;
    }

    public boolean isIn(final Player player) {
        return this.isIn(player.getLocation());
    }

    public boolean isInWithMarge(final Location loc, final double marge) {
        return loc.getWorld() == this.world && loc.getX() >= this.xMinCentered - marge
                && loc.getX() <= this.xMaxCentered + marge && loc.getY() >= this.yMinCentered - marge
                && loc.getY() <= this.yMaxCentered + marge && loc.getZ() >= this.zMinCentered - marge
                && loc.getZ() <= this.zMaxCentered + marge;
    }

    /**
     * Get a list of the chunks which are fully or partially contained in this cuboid.
     *
     * @return A list of Chunk objects
     */
    public Set<Chunk> getChunks() {
        Set<Chunk> res = new HashSet<>();

        World w = this.world;
        int x1 = this.xMin & ~0xf;
        int x2 = this.xMax & ~0xf;
        int z1 = this.zMin & ~0xf;
        int z2 = this.zMax & ~0xf;
        for (int x = x1; x <= x2; x += 16) {
            for (int z = z1; z <= z2; z += 16) {
                res.add(w.getChunkAt(x >> 4, z >> 4));
            }
        }
        return res;
    }

    /**
     * Should get the faces of the cuboid
     *
     * @return List of cuboids
     */
    public List<Cuboid> getFaces() {
        List<Cuboid> faces = new ArrayList<>();
        int x1 = xMin;
        int y1 = yMin;
        int z1 = zMin;
        int x2 = xMax;
        int y2 = yMax;
        int z2 = zMax;

        faces.add(Cuboid.of(new Vector(x1, y1, z1), new Vector(x2, y2, z1), world));
        faces.add(Cuboid.of(new Vector(x1, y1, z1), new Vector(x1, y2, z2), world));
        faces.add(Cuboid.of(new Vector(x2, y1, z1), new Vector(x2, y2, z2), world));
        faces.add(Cuboid.of(new Vector(x1, y1, z2), new Vector(x2, y2, z2), world));
        faces.add(Cuboid.of(new Vector(x1, y1, z1), new Vector(x2, y1, z2), world));
        faces.add(Cuboid.of(new Vector(x1, y2, z1), new Vector(x2, y2, z2), world));
        return faces;
    }

    public Vector getRelative(Location location) {
        return location.clone().subtract(getPoint1()).toVector();
    }

    /**
     * Will draw lines between connected edges.
     * Each location represents a dot of a dotted line.
     *
     * @param distance The distance between each particle
     * @return The list of locations
     */
    public List<Location> drawEdges(double distance) {
        List<Location> result = new ArrayList<>();
        for (double x = xMin; x <= xMax; x += distance) {
            for (double y = yMin; y <= yMax; y += distance) {
                for (double z = zMin; z <= zMax; z += distance) {
                    int components = 0;
                    if (x == xMin || x == xMax) components++;
                    if (y == yMin || y == yMax) components++;
                    if (z == zMin || z == zMax) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }
        return result;
    }
}