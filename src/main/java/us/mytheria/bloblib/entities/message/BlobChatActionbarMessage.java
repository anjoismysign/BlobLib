package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.translatable.BlobTranslatableSnippet;

import java.util.function.Function;

/**
 * @author anjoismysign
 * An instance of BlobMessage that holds a Chat message and an Actionbar message.
 */
public class BlobChatActionbarMessage extends BlobChatMessage {
    private final String actionbar;

    /**
     * Creates a new BlobChatActionbarMessage
     *
     * @param chat       The chat message
     * @param hover      The hover message
     * @param actionbar  The actionbar message
     * @param sound      The sound to play
     * @param locale     The locale to use
     * @param clickEvent The click event to use
     */
    public BlobChatActionbarMessage(@NotNull String chat,
                                    @Nullable String hover,
                                    @NotNull String actionbar,
                                    @Nullable BlobSound sound,
                                    String locale,
                                    @Nullable ClickEvent clickEvent) {
        super(chat, hover, sound, locale, clickEvent);
        this.actionbar = BlobTranslatableSnippet.PARSE(actionbar, locale);
    }

    /**
     * Sends the message to the player
     *
     * @param player The player to send the message to
     */
    @Override
    public void send(Player player) {
        super.send(player);
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
            commandSender.sendMessage(actionbar);
        }
    }

    /**
     * @param function the function to apply to the chat and actionbar message
     * @return a new BlobChatActionbarMessage with the modified chat and actionbar message
     */
    @Override
    public @NotNull BlobChatActionbarMessage modify(Function<String, String> function) {
        return new BlobChatActionbarMessage(function.apply(chat),
                hover == null ? null : function.apply(hover),
                function.apply(actionbar),
                getSound(),
                getLocale(),
                getClickEvent());
    }

    @Override
    @NotNull
    public BlobChatActionbarMessage onClick(ClickEvent event) {
        return new BlobChatActionbarMessage(chat, hover, actionbar, getSound(),
                getLocale(), event);
    }
}
