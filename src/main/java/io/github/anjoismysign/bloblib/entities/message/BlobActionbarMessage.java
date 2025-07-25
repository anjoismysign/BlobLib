package io.github.anjoismysign.bloblib.entities.message;

import io.github.anjoismysign.bloblib.entities.translatable.BlobTranslatableSnippet;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * A message that holds an Actionbar message
 */
public class BlobActionbarMessage extends AbstractMessage {
    private final String actionbar;

    /**
     * @param reference The reference to the message
     * @param message   The message to send
     * @param sound     The sound to play
     * @param locale    The locale of the message
     */
    public BlobActionbarMessage(@NotNull String reference,
                                @NotNull String message,
                                @Nullable BlobSound sound,
                                @NotNull String locale) {
        super(reference, sound, locale, null);
        this.actionbar = BlobTranslatableSnippet.PARSE(message, locale);
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
        return new BlobActionbarMessage(identifier(), function.apply(actionbar), getSound(), locale());
    }
}
