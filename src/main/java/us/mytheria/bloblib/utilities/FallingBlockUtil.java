package us.mytheria.bloblib.utilities;

import org.bukkit.Sound;
import org.bukkit.SoundGroup;
import org.bukkit.entity.FallingBlock;

import java.util.function.Function;

public class FallingBlockUtil {

    public static void playSoundFromSoundGroup(FallingBlock fallingBlock, Function<SoundGroup, Sound> function,
                                               float volume, float pitch) {
        fallingBlock.getWorld().playSound(fallingBlock.getLocation(), function.apply(fallingBlock.getBlockData()
                .getSoundGroup()), volume, pitch);
    }

    public static void playBreakSound(FallingBlock fallingBlock, float volume, float pitch) {
        playSoundFromSoundGroup(fallingBlock, SoundGroup::getBreakSound, volume, pitch);
    }

    public static void playPlaceSound(FallingBlock fallingBlock, float volume, float pitch) {
        playSoundFromSoundGroup(fallingBlock, SoundGroup::getPlaceSound, volume, pitch);
    }

    public static void playHitSound(FallingBlock fallingBlock, float volume, float pitch) {
        playSoundFromSoundGroup(fallingBlock, SoundGroup::getHitSound, volume, pitch);
    }

    public static void playStepSound(FallingBlock fallingBlock, float volume, float pitch) {
        playSoundFromSoundGroup(fallingBlock, SoundGroup::getStepSound, volume, pitch);
    }

    public static void playFallSound(FallingBlock fallingBlock, float volume, float pitch) {
        playSoundFromSoundGroup(fallingBlock, SoundGroup::getFallSound, volume, pitch);
    }

    public static void playBreakSound(FallingBlock fallingBlock) {
        playBreakSound(fallingBlock, 1, 1);
    }

    public static void playPlaceSound(FallingBlock fallingBlock) {
        playPlaceSound(fallingBlock, 1, 1);
    }

    public static void playHitSound(FallingBlock fallingBlock) {
        playHitSound(fallingBlock, 1, 1);
    }

    public static void playStepSound(FallingBlock fallingBlock) {
        playStepSound(fallingBlock, 1, 1);
    }

    public static void playFallSound(FallingBlock fallingBlock) {
        playFallSound(fallingBlock, 1, 1);
    }


}
