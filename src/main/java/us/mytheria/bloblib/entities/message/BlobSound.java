package us.mytheria.bloblib.entities.message;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public record BlobSound(Sound sound, float volume, float pitch, @Nullable SoundCategory soundCategory) {
    public void play(Player player) {
        if (soundCategory == null)
            player.playSound(player.getLocation(), sound, volume, pitch);
        else
            player.playSound(player.getLocation(), sound, soundCategory, volume, pitch);
    }
}
