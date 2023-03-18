package us.mytheria.bloblib.entities.message;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record BlobSound(Sound sound, float volume, float pitch,
                        @Nullable SoundCategory soundCategory,
                        @NotNull MessageAudience audience) {
    /**
     * Plays the sound to the player at the given location
     *
     * @param player   The player to play the sound to
     * @param location The location to play the sound at
     */
    public void play(Player player, Location location) {
        if (soundCategory == null)
            player.playSound(location, sound, volume, pitch);
        else
            player.playSound(location, sound, soundCategory, volume, pitch);
    }

    /**
     * Plays the sound to the player at the player's location
     *
     * @param player The player to play the sound to
     */
    public void play(Player player) {
        play(player, player.getLocation());
    }

    /**
     * Plays the sound to the world at the given location
     *
     * @param location The location to play the sound at
     */
    public void playInWorld(Location location) {
        if (soundCategory == null)
            location.getWorld().playSound(location, sound, volume, pitch);
        else
            location.getWorld().playSound(location, sound, soundCategory, volume, pitch);
    }

    /**
     * Handles the sound at the given location
     *
     * @param player   The player that's linked to the sound
     * @param location The location to play the sound at
     */
    public void handle(Player player, Location location) {
        if (audience == MessageAudience.PLAYER)
            play(player, location);
        else
            playInWorld(location);
    }

    /**
     * Handles the sound at the player's location
     *
     * @param player The player that's linked to the sound
     */
    public void handle(Player player) {
        if (audience == MessageAudience.PLAYER)
            play(player);
        else
            playInWorld(player.getLocation());
    }
}
