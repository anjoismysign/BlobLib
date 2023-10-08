package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.mytheria.bloblib.entities.translatable.BlobTranslatableSnippet;

import java.util.function.Function;

/**
 * @author anjoismysign
 * An instance of BlobMessage that holds a Chat message.
 */
public class BlobChatMessage extends SerialBlobMessage {
    /**
     * The chat message
     */
    protected final String chat;

    /**
     * Creates a new BlobChatMessage
     *
     * @param message The chat message
     * @param sound   The sound to play
     */
    public BlobChatMessage(String message, BlobSound sound, String locale) {
        super(sound, locale);
        this.chat = BlobTranslatableSnippet.PARSE(message, locale);
    }

    /**
     * Sends the message to the player
     *
     * @param player The player to send the message to
     */
    @Override
    public void send(Player player) {
        player.sendMessage(chat);
    }

    /**
     * Sends the message to the command sender
     *
     * @param sender The command sender to send the message to
     */
    public void send(CommandSender sender) {
        sender.sendMessage(chat);
    }

    /**
     * Sends the message to the command sender
     *
     * @param commandSender The command sender to send the message to
     */
    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            handle(player);
        else
            commandSender.sendMessage(chat);
    }

    /**
     * @param function the function to apply to the chat message
     * @return a new BlobChatMessage with the modified chat message
     */
    @Override
    public @NotNull BlobChatMessage modify(Function<String, String> function) {
        return new BlobChatMessage(function.apply(chat), getSound(),
                getLocale());
    }
}
