package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.translatable.BlobTranslatableSnippet;

import java.util.function.Function;

/**
 * @author anjoismysign
 * An instance of BlobMessage that holds a Chat message.
 */
public class BlobChatMessage extends AbstractMessage {
    /**
     * The chat message
     */
    @Nullable
    protected final String hover;
    @NotNull
    protected final String chat;

    /**
     * Creates a new BlobChatMessage
     *
     * @param reference  The reference of the message
     * @param message    The chat message
     * @param hover      The hover message
     * @param sound      The sound to play
     * @param locale     The locale to use
     * @param clickEvent The click event to use
     */
    public BlobChatMessage(@NotNull String reference,
                           @NotNull String message,
                           @Nullable String hover,
                           @Nullable BlobSound sound,
                           @NotNull String locale,
                           @Nullable ClickEvent clickEvent) {
        super(reference, sound, locale, clickEvent);
        this.chat = BlobTranslatableSnippet.PARSE(message, locale);
        this.hover = hover == null ? null : BlobTranslatableSnippet.PARSE(hover, locale);
    }

    /**
     * Sends the message to the player
     *
     * @param player The player to send the message to
     */
    @Override
    public void send(Player player) {
        if (hover == null)
            player.sendMessage(chat);
        else {
            TextComponent component = new TextComponent(TextComponent.fromLegacyText(chat));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new Text(hover)));
            ClickEvent clickEvent = getClickEvent();
            if (clickEvent != null)
                component.setClickEvent(clickEvent);
            player.spigot().sendMessage(component);
        }
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
        return new BlobChatMessage(identifier(), function.apply(chat),
                hover == null ? null : function.apply(hover),
                getSound(),
                locale(),
                getClickEvent());
    }

    @Override
    @NotNull
    public BlobChatMessage onClick(ClickEvent event) {
        return new BlobChatMessage(identifier(), chat, hover, getSound(), locale(), event);
    }
}
