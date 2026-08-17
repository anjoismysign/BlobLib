package io.github.anjoismysign.bloblib.message;

import io.github.anjoismysign.bloblib.translatable.BlobTranslatableSnippet;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * @author anjoismysign
 * An instance of BlobMessage that holds a Chat message and an Actionbar message.
 */
public class BlobChatActionbarMessage extends BlobChatMessage {
    @NotNull
    private final String actionbar;

    /**
     * Creates a new BlobChatActionbarMessage
     *
     * @param reference  The reference to the message
     * @param chat       The chat message
     * @param hover      The hover message
     * @param actionbar  The actionbar message
     * @param sound      The sound to play
     * @param locale     The locale to use
     * @param clickEvent The click event to use
     */
    public BlobChatActionbarMessage(@NotNull String reference,
                                    @NotNull String chat,
                                    @Nullable String hover,
                                    @NotNull String actionbar,
                                    @Nullable BlobSound sound,
                                    @NotNull String locale,
                                    @Nullable ClickEvent clickEvent) {
        super(reference, chat, hover, sound, locale, clickEvent);
        this.actionbar = actionbar;
    }

    /**
     * Sends the message to the player
     *
     * @param player The player to send the message to
     */
    @Override
    public void send(Player player) {
        super.send(player);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(BlobTranslatableSnippet.PARSE(actionbar, locale())));
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
            commandSender.sendMessage(BlobTranslatableSnippet.PARSE(chat, locale()));
            commandSender.sendMessage(BlobTranslatableSnippet.PARSE(actionbar, locale()));
        }
    }

    /**
     * @param function the function to apply to the chat and actionbar message
     * @return a new BlobChatActionbarMessage with the modified chat and actionbar message
     */
    @ApiStatus.Internal
    @Override
    public @NotNull BlobChatActionbarMessage modify(Function<String, String> function) {
        return new BlobChatActionbarMessage(identifier(), function.apply(chat),
                hover == null ? null : function.apply(hover),
                function.apply(actionbar),
                getSound(),
                locale(),
                getClickEvent());
    }

    @Override
    @NotNull
    public BlobChatActionbarMessage onClick(ClickEvent event) {
        return new BlobChatActionbarMessage(identifier(), chat, hover, actionbar, getSound(),
                locale(), event);
    }

    @Override
    public ModernMessage toModernMessage(){
        return new ModernMessage(identifier(), chat, hover, actionbar, null, null, -1, -1, -1, getSound(), locale(), getClickEvent());
    }
}
