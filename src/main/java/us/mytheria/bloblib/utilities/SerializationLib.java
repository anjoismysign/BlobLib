package us.mytheria.bloblib.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.exception.ConfigurationFieldException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SerializationLib {
    private static SerializationLib instance;
    private final ExecutorService service;

    private SerializationLib() {
        this.service = Executors.newCachedThreadPool();
    }


    public void shutdown() {
        service.shutdown();
    }

    public static SerializationLib getInstance() {
        if (instance == null)
            instance = new SerializationLib();
        return instance;
    }

    public static String serialize(PotionEffect effect) {
        return PotionEffectLib.stringSerialize(effect);
    }

    public static PotionEffect deserializePotionEffect(String string) {
        return PotionEffectLib.deserializeString(string);
    }

    public static String serialize(Color color) {
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    public static Color deserializeColor(String string) {
        String[] split = string.split(",");
        return Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    public static String serialize(Vector vector) {
        return vector.getX() + "," + vector.getY() + "," + vector.getZ();
    }

    public static Vector deserializeVector(String string) {
        String[] split = string.split(",");
        return new Vector(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
    }

    public static String serialize(BlockVector vector) {
        return vector.getBlockX() + "," + vector.getBlockY() + "," + vector.getBlockZ();
    }

    public static BlockVector deserializeBlockVector(String string) {
        String[] split = string.split(",");
        return new BlockVector(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    public static String serialize(OfflinePlayer player) {
        return serialize(player.getUniqueId());
    }

    public static OfflinePlayer deserializeOfflinePlayer(String string) {
        return Bukkit.getOfflinePlayer(deserializeUUID(string));
    }

    public static String serialize(UUID uuid) {
        return uuid.toString();
    }

    public static UUID deserializeUUID(String string) {
        return UUID.fromString(string);
    }

    public static String serialize(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() +
                "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    @NotNull
    public static Location deserializeLocation(String string) {
        String[] split = string.split(",");
        if (split.length == 4)
            return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
        else if (split.length == 6)
            return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
        else
            throw new IllegalArgumentException("Invalid location string: " + string);
    }

    public static String serialize(Block block) {
        return block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();
    }

    public static Block deserializeBlock(String string) {
        String[] split = string.split(",");
        return Bukkit.getWorld(split[0]).getBlockAt(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }

    public static String serialize(BigInteger bigInteger) {
        return bigInteger.toString();
    }

    public static BigInteger deserializeBigInteger(String string) {
        return new BigInteger(string);
    }

    public static String serialize(BigDecimal bigDecimal) {
        return bigDecimal.toString();
    }

    public static BigDecimal deserializeBigDecimal(String string) {
        return new BigDecimal(string);
    }

    public static String serialize(World world) {
        return world.getName();
    }

    /**
     * Will attempt to deserialize a world for
     * a minute. If fails, will throw a RuntimeException.
     *
     * @param string World name
     * @param period period to check for world
     * @return World
     */
    @NotNull
    public static World deserializeWorld(String string, int period) {
        Future<World> future = getInstance().service.submit(() -> {
            CompletableFuture<World> completableFuture = new CompletableFuture<>();
            new BukkitRunnable() {
                @Override
                public void run() {
                    World world = Bukkit.getWorld(string);
                    if (completableFuture.isDone() || completableFuture.isCancelled() || completableFuture.isCompletedExceptionally())
                        cancel();
                    if (world == null)
                        return;
                    completableFuture.complete(world);
                    cancel();
                }
            }.runTaskTimerAsynchronously(BlobLib.getInstance(), 0, period);
            World world;
            try {
                world = completableFuture.get(period, TimeUnit.SECONDS); // period is in ticks but in CompletableFuture#get is in seconds
            } catch ( Exception exception ) {
                world = null;
            }
            return world;
        });
        try {
            return future.get();
        } catch ( Exception exception ) {
            throw new ConfigurationFieldException("World " + string + " not found after waiting 60 seconds!");
        }
    }

    /**
     * Will attempt to deserialize a world for
     * a minute. If fails, will throw a RuntimeException.
     *
     * @param string World name
     * @return the World
     */
    public static World deserializeWorld(String string) {
        return deserializeWorld(string, 15);
    }
}