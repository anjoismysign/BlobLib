package us.mytheria.bloblib.entities.message;

import me.anjoismysign.holoworld.asset.DataAsset;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.api.BlobLibSoundAPI;
import us.mytheria.bloblib.entities.DataAssetType;

import java.util.Objects;

public record BlobSound(Sound sound,
                        float volume,
                        float pitch,
                        @Nullable Long seed,
                        @Nullable SoundCategory soundCategory,
                        @NotNull MessageAudience audience,
                        @NotNull String identifier)
        implements DataAsset {

    /**
     * Gets a BlobSound by its key.
     *
     * @param key The key of the sound
     * @return The sound. Null if not found
     */
    @Nullable
    public static BlobSound by(@NotNull String key) {
        Objects.requireNonNull(key);
        return BlobLibSoundAPI.getInstance().getSound(key);
    }

    /**
     * Plays the sound to the player at the given location
     *
     * @param player   The player to play the sound to
     * @param location The location to play the sound at
     */
    public void play(Player player, Location location) {
        if (soundCategory == null) {
            if (seed == null)
                player.playSound(location, sound, volume, pitch);
            else
                player.playSound(location, sound, SoundCategory.MASTER, volume, pitch, seed);
        } else {
            if (seed == null)
                player.playSound(location, sound, soundCategory, volume, pitch);
            else
                player.playSound(location, sound, soundCategory, volume, pitch, seed);
        }
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
        if (soundCategory == null) {
            if (seed == null)
                location.getWorld().playSound(location, sound, volume, pitch);
            else
                location.getWorld().playSound(location, sound, SoundCategory.MASTER, volume, pitch, seed);
        } else {
            if (seed == null)
                location.getWorld().playSound(location, sound, soundCategory, volume, pitch);
            else
                location.getWorld().playSound(location, sound, soundCategory, volume, pitch, seed);
        }
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

    /**
     * Handles the sound at the entity's location
     * If the entity is a player, it will play the sound to the player
     * or to the world depending on the audience
     *
     * @param entity The entity that's linked to the sound
     */
    public void handle(Entity entity) {
        EntityType entityType = entity.getType();
        if (entityType == EntityType.PLAYER) {
            Player player = (Player) entity;
            handle(player);
            return;
        }
        playInWorld(entity.getLocation());
    }

    /**
     * Handles the sound at the block's location
     *
     * @param block The block that's linked to the sound
     */
    public void handle(Block block) {
        playInWorld(block.getLocation());
    }

    /**
     * Will handle the sound to all online players.
     */
    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(this::handle);
    }

    public DataAssetType getType() {
        return DataAssetType.BLOB_SOUND;
    }
}
