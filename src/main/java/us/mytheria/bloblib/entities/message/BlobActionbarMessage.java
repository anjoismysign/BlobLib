package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * A message that holds an Actionbar message
 */
public class BlobActionbarMessage extends SerialBlobMessage {
    private final String actionbar;

    /**
     * @param message The message to send
     * @param sound   The sound to play
     */
    public BlobActionbarMessage(String message, BlobSound sound,
                                String locale) {
        super(sound, locale);
        this.actionbar = message;
    }

    /**
     * @param message The message to send
     */
    public BlobActionbarMessage(String message) {
        this(message, null, null);
    }

    /**
     * @param player The player to send the message to
     */
    @Override
    public void send(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
    }

    /**
     * @param commandSender The command sender to send the message to
     */
    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            handle(player);
        else
            commandSender.sendMessage(actionbar);
    }

    /**
     * @param function The function to modify the message with
     * @return A new message with the modified message
     */
    @Override
    public @NotNull BlobActionbarMessage modify(Function<String, String> function) {
        return new BlobActionbarMessage(function.apply(actionbar), getSound(), getLocale());
    }
}
