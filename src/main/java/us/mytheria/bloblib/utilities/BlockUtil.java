package us.mytheria.bloblib.utilities;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.function.Function;


public class BlockUtil {

    public static void set(Location beginning, Vector vector1, Vector vector2, Material material) {
        World world = beginning.getWorld();
        Location l1 = beginning.clone();
        l1.add(vector1);
        Location l2 = beginning.clone();
        l2.add(vector2);

        int x1 = Integer.min(l1.getBlockX(), l2.getBlockX());
        int x2 = Integer.max(l1.getBlockX(), l2.getBlockX());
        int y1 = Integer.min(l1.getBlockY(), l2.getBlockY());
        int y2 = Integer.max(l1.getBlockY(), l2.getBlockY());
        int z1 = Integer.min(l1.getBlockZ(), l2.getBlockZ());
        int z2 = Integer.max(l1.getBlockZ(), l2.getBlockZ());
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++)
                    world.setType(x, y, z, material);
            }
        }
    }

    public static void setLight(Location beginning, Vector vector1, Vector vector2) {
        set(beginning, vector1, vector2, Material.LIGHT);
    }

    public static void set(World world, Vector pos1, Vector pos2, Material material) {
        int x1 = Integer.min(pos1.getBlockX(), pos2.getBlockX());
        int x2 = Integer.max(pos1.getBlockX(), pos2.getBlockX());
        int y1 = Integer.min(pos1.getBlockY(), pos2.getBlockY());
        int y2 = Integer.max(pos1.getBlockY(), pos2.getBlockY());
        int z1 = Integer.min(pos1.getBlockZ(), pos2.getBlockZ());
        int z2 = Integer.max(pos1.getBlockZ(), pos2.getBlockZ());
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++)
                    world.setType(x, y, z, material);
            }
        }
    }

    public static void set(Location pos1, Location pos2, Material material) {
        set(pos1.getWorld(), pos1.toVector(), pos2.toVector(), material);
    }

    public static void setLight(Location pos1, Location pos2) {
        set(pos1.getWorld(), pos1.toVector(), pos2.toVector(), Material.LIGHT);
    }

    public static void playSoundFromSoundGroup(Block block, Function<SoundGroup, Sound> soundGroupConsumer,
                                               float volume, float pitch) {
        block.getWorld().playSound(block.getLocation(), soundGroupConsumer.apply(block.getBlockData()
                .getSoundGroup()), volume, pitch);
    }

    public static void playSoundFromSoundGroup(Block block, Function<SoundGroup, Sound> soundGroupConsumer) {
        playSoundFromSoundGroup(block, soundGroupConsumer, 1, 1);
    }

    public static void playPlaceSound(Block block) {
        playSoundFromSoundGroup(block, SoundGroup::getPlaceSound);
    }

    public static void playBreakSound(Block block) {
        playSoundFromSoundGroup(block, SoundGroup::getBreakSound);
    }

    public static void playStepSound(Block block) {
        playSoundFromSoundGroup(block, SoundGroup::getStepSound);
    }

    public static void playHitSound(Block block) {
        playSoundFromSoundGroup(block, SoundGroup::getHitSound);
    }

    public static void playFallSound(Block block) {
        playSoundFromSoundGroup(block, SoundGroup::getFallSound);
    }

}
