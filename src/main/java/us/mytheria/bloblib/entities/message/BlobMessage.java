package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.BlobMessageModder;
import us.mytheria.bloblib.entities.Localizable;

import java.util.function.Function;

/**
 * @author anjoismysign
 * A BlobMessage is a wrapper for most types of messages that Minecraft client
 * makes use of. This includes chat messages, actionbar messages, title messages
 * and sounds. Bossbar messages are not included in this interface but may be
 * part of a future update. BlobMessages are meant to be able to be written in
 * YAML files and be parsed into a BlobMessage object (which instance is a
 * SerialBlobMessage) and be stored in random access memory (RAM) for later
 * being retrieved (which instance is a ReferenceBlobMessage).
 * There are also utility methods to modify the message, such as replacing
 * all types of texts in multiple types of messages (since you can have multiple
 * types of messages in a BlobMessage, such as a BlobChatTitleMessage) while at
 * the same time being able to import your own SerialBlobMessage's in your own
 * plugin and store them in the same way as the BlobMessages in BlobLib so other
 * plugins and even the same server administrator can use them.
 */
public interface BlobMessage extends Localizable {
    /**
     * A click event that will be executed if BlobMessage has a chat message instance and is clicked.
     *
     * @return The click event. Null if there is no click event.
     */
    @Nullable
    ClickEvent getClickEvent();

    /**
     * Will send the message to the player.
     *
     * @param player The player to send the message to
     * @deprecated Use {@link #handle(Player)} instead
     */
    @Deprecated
    void send(Player player);

    /**
     * If sound is not null, it will play the sound to the player.
     *
     * @param player   The player to send the message to
     * @param location The location to play the sound at
     * @deprecated Use {@link #handle(Player)} instead
     */
    @Deprecated
    default void sendAndPlay(Player player, Location location) {
        send(player);
        if (getSound() != null)
            getSound().play(player, location);
    }

    /**
     * If sound is not null, it will play the sound to the player.
     * Would be played at the player's location.
     *
     * @param player The player to send the message to
     * @deprecated Use {@link #handle(Player)} instead
     */
    @Deprecated
    default void sendAndPlay(Player player) {
        sendAndPlay(player, player.getLocation());
    }

    /**
     * If the sound is not null, it will play the sound at player's location
     * and nearby players will be able to hear it.
     *
     * @param player   The player to send the message to
     * @param location The location to play the sound at
     * @deprecated Use {@link #handle(Player)} instead
     */
    @Deprecated
    default void sendAndPlayInWorld(Player player, Location location) {
        send(player);
        if (getSound() != null)
            getSound().playInWorld(location);
    }

    /**
     * If the sound is not null, it will play the sound at player's location
     * and nearby players will be able to hear it.
     * Would be played at the player's location.
     *
     * @param player The player to send the message to
     * @deprecated Use {@link #handle(Player)} instead
     */
    @Deprecated
    default void sendAndPlayInWorld(Player player) {
        sendAndPlayInWorld(player, player.getLocation());
    }

    /**
     * Will handle the message with the required settings for the player
     * such as not having a sound, if having sound playing just
     * to the player, or playing to the whole world, etc.
     *
     * @param player   The player to handle the message for
     * @param location The location to play the sound at
     */
    default void handle(Player player, Location location) {
        if (getSound() == null)
            send(player);
        else if (getSound().audience() == MessageAudience.PLAYER)
            sendAndPlay(player, location);
        else
            sendAndPlayInWorld(player, location);
    }

    /**
     * Will handle the message with the required settings for the player
     * such as not having a sound, if having sound playing just
     * to the player, or playing to the whole world, etc.
     * Would be played at the player's location.
     *
     * @param player The player to handle the message for
     */
    default void handle(Player player) {
        handle(player, player.getLocation());
    }

    /**
     * Will handle the message to all online players.
     *
     * @deprecated Use @link {@link us.mytheria.bloblib.api.BlobLibMessageAPI#broadcast(String)} instead
     */
    @Deprecated
    default void broadcast() {
        Bukkit.getOnlinePlayers().forEach(this::handle);
    }

    /**
     * Will send the message to the command sender.
     *
     * @param commandSender The command sender to send the message to
     */
    void toCommandSender(CommandSender commandSender);

    /**
     * Will retrieve the BlobSound object.
     *
     * @return The sound to play
     */
    @Nullable
    BlobSound getSound();

    /**
     * @param function The function to modify the message with
     * @return A new message with the modified message
     */
    @NotNull
    BlobMessage modify(Function<String, String> function);

    /**
     * Will return a new BlobMessage with the click event set to the given click event.
     *
     * @param clickEvent The click event to set
     * @return A new BlobMessage with the click event set to the given click event
     */
    @NotNull
    BlobMessage onClick(ClickEvent clickEvent);

    @NotNull
    default BlobMessageModder<BlobMessage> modder() {
        return BlobMessageModder.mod(this);
    }
}
