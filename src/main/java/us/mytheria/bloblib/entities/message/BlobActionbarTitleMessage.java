package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.translatable.BlobTranslatableSnippet;

import java.util.function.Function;

/**
 * @author anjoismysign
 * A BlobMessage instance that holds a Title/Subtitle and an Actionbar message.
 */
public class BlobActionbarTitleMessage extends BlobTitleMessage {
    @NotNull
    private final String actionbar;

    /**
     * @param reference The reference of the message
     * @param actionbar The actionbar message to send
     * @param title     The title to send
     * @param subtitle  The subtitle to send
     * @param fadeIn    The time it takes for the title to fade in
     * @param stay      The time the title stays on the screen
     * @param fadeOut   The time it takes for the title to fade out
     * @param sound     The sound to play
     * @param locale    The locale of the message
     */
    public BlobActionbarTitleMessage(@NotNull String reference,
                                     @NotNull String actionbar,
                                     @NotNull String title,
                                     @NotNull String subtitle,
                                     int fadeIn,
                                     int stay,
                                     int fadeOut,
                                     @Nullable BlobSound sound,
                                     @NotNull String locale) {
        super(reference, title, subtitle, fadeIn, stay, fadeOut, sound, locale);
        this.actionbar = BlobTranslatableSnippet.PARSE(actionbar, locale);
    }

    /**
     * @param player The player to send the message to
     */
    @Override
    public void send(Player player) {
        super.send(player);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionbar));
    }

    /**
     * @param commandSender The command sender to send the message to
     */
    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            handle(player);
        else {
            commandSender.sendMessage(title);
            commandSender.sendMessage(subtitle);
            commandSender.sendMessage(actionbar);
        }
    }

    /**
     * @param function The function to modify the message with
     * @return A new message with the modified message
     */
    @Override
    public @NotNull BlobActionbarTitleMessage modify(Function<String, String> function) {
        return new BlobActionbarTitleMessage(getReference(), function.apply(actionbar), function.apply(title),
                function.apply(subtitle), fadeIn, stay, fadeOut, getSound(),
                getLocale());
    }
}
