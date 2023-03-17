package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author anjoismysign
 * An instance of BlobMessage that holds a Chat message, an Actionbar message
 * and a Title/Subtitle message.
 */
public class BlobChatActionbarTitleMessage extends BlobChatMessage {
    private final String title, subtitle, actionbar;
    private final int fadeIn, stay, fadeOut;

    /**
     * Creates a new BlobChatActionbarMessage
     *
     * @param chat      The chat message
     * @param actionbar The actionbar message
     * @param fadeIn    The time in ticks for the title to fade in
     * @param fadeOut   The time in ticks for the title to fade out
     * @param stay      The time in ticks for the title to stay
     * @param title     The title message
     * @param subtitle  The subtitle message
     * @param sound     The sound to play
     */
    public BlobChatActionbarTitleMessage(String chat, String actionbar, String title, String subtitle, int fadeIn, int stay, int fadeOut,
                                         BlobSound sound) {
        super(chat, sound);
        this.actionbar = actionbar;
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    /**
     * Sends the message to the player
     *
     * @param player The player to send the message to
     */
    @Override
    public void send(Player player) {
        super.send(player);
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
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
        else {
            commandSender.sendMessage(chat);
            commandSender.sendMessage(title);
            commandSender.sendMessage(subtitle);
            commandSender.sendMessage(actionbar);
        }
    }

    /**
     * @param function the function to apply to the chat and actionbar message
     * @return a new BlobChatActionbarMessage with the modified chat and actionbar message
     */
    @Override
    public @NotNull BlobChatActionbarTitleMessage modify(Function<String, String> function) {
        return new BlobChatActionbarTitleMessage(function.apply(chat), function.apply(actionbar), function.apply(title),
                function.apply(subtitle), fadeIn, stay, fadeOut, getSound());
    }
}
