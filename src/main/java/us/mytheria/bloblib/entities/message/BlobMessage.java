package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public interface BlobMessage {
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
     * @param player The player to send the message to
     * @deprecated Use {@link #handle(Player)} instead
     */
    @Deprecated
    default void sendAndPlay(Player player) {
        send(player);
        if (getSound() != null)
            getSound().play(player);
    }

    /**
     * If the sound is not null, it will play the sound at player's location
     * and nearby players will be able to hear it.
     *
     * @param player The player to send the message to
     * @deprecated Use {@link #handle(Player)} instead
     */
    @Deprecated
    default void sendAndPlayInWorld(Player player) {
        send(player);
        if (getSound() != null)
            getSound().playInWorld(player.getLocation());
    }

    /**
     * Will handle the message with the required settings for the player
     * such as not having a sound, if having sound playing just
     * to the player, or playing to the whole world, etc.
     *
     * @param player The player to handle the message for
     */
    default void handle(Player player) {
        if (getSound() == null)
            send(player);
        else if (getSound().audience() == MessageAudience.PLAYER)
            sendAndPlay(player);
        else
            sendAndPlayInWorld(player);
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
}
