package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.translatable.BlobTranslatableSnippet;

import java.util.function.Function;

/**
 * @author anjoismysign
 * An instance of BlobMessage that holds a Chat message and
 * a Title/Subtitle message
 */
public class BlobChatTitleMessage extends BlobChatMessage {
    @NotNull
    private final String title, subtitle;
    private final int fadeIn, stay, fadeOut;

    /**
     * @param chat     the chat message
     * @param title    the title message
     * @param subtitle the subtitle message
     * @param fadeIn   the time in ticks for the title to fade in
     * @param stay     the time in ticks for the title to stay
     * @param fadeOut  the time in ticks for the title to fade out
     * @param sound    the sound to play
     */
    public BlobChatTitleMessage(@NotNull String reference,
                                @NotNull String chat,
                                @Nullable String hover,
                                @NotNull String title,
                                @NotNull String subtitle,
                                int fadeIn,
                                int stay,
                                int fadeOut,
                                @Nullable BlobSound sound,
                                @NotNull String locale,
                                @Nullable ClickEvent clickEvent) {
        super(reference, chat, hover, sound, locale, clickEvent);
        this.title = BlobTranslatableSnippet.PARSE(title, locale);
        this.subtitle = BlobTranslatableSnippet.PARSE(subtitle, locale);
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    /**
     * @param player the player to send the message to
     */
    @Override
    public void send(Player player) {
        super.send(player);
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    /**
     * @param commandSender the command sender to send the message to
     */
    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            handle(player);
        else {
            commandSender.sendMessage(chat);
            commandSender.sendMessage(title);
            commandSender.sendMessage(subtitle);
        }
    }

    /**
     * @param function the function to modify the message
     * @return a new BlobChatTitleMessage with the modified message
     */
    @Override
    public @NotNull BlobChatTitleMessage modify(Function<String, String> function) {
        return new BlobChatTitleMessage(getReference(), function.apply(chat),
                hover == null ? null : function.apply(hover),
                function.apply(title),
                function.apply(subtitle),
                fadeIn, stay, fadeOut,
                getSound(),
                getLocale(),
                getClickEvent());
    }

    @Override
    @NotNull
    public BlobChatTitleMessage onClick(ClickEvent event) {
        return new BlobChatTitleMessage(getReference(), chat, hover, title, subtitle, fadeIn, stay,
                fadeOut, getSound(), getLocale(), event);
    }
}
