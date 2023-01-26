package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
     * @param player The player to send the message to
     */
    void send(Player player);

    /**
     * @param player The player to send the message to
     */
    void sendAndPlay(Player player);

    /**
     * @param commandSender The command sender to send the message to
     */
    void toCommandSender(CommandSender commandSender);

    /**
     * @return The sound to play
     */
    BlobSound getSound();

    /**
     * @param function The function to modify the message with
     * @return A new message with the modified message
     */
    BlobMessage modify(Function<String, String> function);
}
