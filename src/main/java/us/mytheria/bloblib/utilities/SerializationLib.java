package us.mytheria.bloblib.utilities;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.BlobLib;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.*;

public class SerializationLib {
    private static SerializationLib instance;
    private final ExecutorService service;

    private SerializationLib() {
        this.service = Executors.newCachedThreadPool();
    }

    public static SerializationLib getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            SerializationLib.instance = new SerializationLib();
        }
        return instance;
    }

    public void shutdown() {
        service.shutdown();
    }

    public static SerializationLib getInstance() {
        return getInstance(null);
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
                world = completableFuture.get(1, TimeUnit.MINUTES);
            } catch (Exception e) {
                world = null;
            }
            return world;
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("World " + string + " not found after waiting 60 seconds!");
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

    public static String serialize(Material material) {
        return material.name();
    }

    public static Material deserializeMaterial(String string) {
        try {
            return Material.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("Material " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(Particle particle) {
        return particle.name();
    }

    public static Particle deserializeParticle(String string) {
        try {
            return Particle.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("Particle " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(EntityType entityType) {
        return entityType.name();
    }

    public static EntityType deserializeEntityType(String string) {
        try {
            return EntityType.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("EntityType " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(DyeColor dyeColor) {
        return dyeColor.name();
    }

    public static DyeColor deserializeDyeColor(String string) {
        try {
            return DyeColor.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("DyeColor " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(GameMode gameMode) {
        return gameMode.name();
    }

    public static GameMode deserializeGameMode(String string) {
        try {
            return GameMode.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("GameMode " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(Enchantment enchantment) {
        return enchantment.getKey().getKey();
    }

    public static Enchantment deserializeEnchantment(String string) {
        try {
            return Enchantment.getByKey(NamespacedKey.minecraft(string));
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("Enchantment " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(CropState cropState) {
        return cropState.name();
    }

    public static CropState deserializeCropState(String string) {
        try {
            return CropState.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("CropState " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(Difficulty difficulty) {
        return difficulty.name();
    }

    public static Difficulty deserializeDifficulty(String string) {
        try {
            return Difficulty.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("Difficulty " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(WeatherType weatherType) {
        return weatherType.name();
    }

    public static WeatherType deserializeWeatherType(String string) {
        try {
            return WeatherType.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("WeatherType " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(Effect effect) {
        return effect.name();
    }

    public static Effect deserializeEffect(String string) {
        try {
            return Effect.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("Effect " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(EntityEffect entityEffect) {
        return entityEffect.name();
    }

    public static EntityEffect deserializeEntityEffect(String string) {
        try {
            return EntityEffect.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("EntityEffect " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(Fluid fluid) {
        return fluid.name();
    }

    public static Fluid deserializeFluid(String string) {
        try {
            return Fluid.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("Fluid " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(Instrument instrument) {
        return instrument.name();
    }

    public static Instrument deserializeInstrument(String string) {
        try {
            return Instrument.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("Instrument " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(MusicInstrument musicInstrument) {
        return musicInstrument.getKey().getKey();
    }

    public static MusicInstrument deserializeMusicInstrument(String string) {
        try {
            return MusicInstrument.getByKey(NamespacedKey.minecraft(string));
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("MusicInstrument " + string + " not found!");
            throw e;
        }
    }

    public static String serialize(TreeType treeType) {
        return treeType.name();
    }

    public static TreeType deserializeTreeType(String string) {
        try {
            return TreeType.valueOf(string);
        } catch (IllegalArgumentException e) {
            BlobLib.getAnjoLogger().error("TreeType " + string + " not found!");
            throw e;
        }
    }
}
