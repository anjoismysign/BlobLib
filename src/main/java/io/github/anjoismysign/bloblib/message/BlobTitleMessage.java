package io.github.anjoismysign.bloblib.message;

import io.github.anjoismysign.bloblib.translatable.BlobTranslatableSnippet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class BlobTitleMessage extends AbstractMessage {
    @NotNull
    protected final String title, subtitle;
    protected final int fadeIn, stay, fadeOut;

    /**
     * @param reference The reference of the message
     * @param title     The title to send
     * @param subtitle  The subtitle to send
     * @param fadeIn    The time it takes for the title to fade in
     * @param stay      The time the title stays on the screen
     * @param fadeOut   The time it takes for the title to fade out
     * @param sound     The sound to play
     * @param locale    The locale of the message
     */
    public BlobTitleMessage(@NotNull String reference,
                            @NotNull String title,
                            @NotNull String subtitle,
                            int fadeIn,
                            int stay,
                            int fadeOut,
                            @Nullable BlobSound sound,
                            @NotNull String locale) {
        super(reference, sound, locale, null);
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void send(Player player) {
        player.sendTitle(BlobTranslatableSnippet.PARSE(title, locale()), BlobTranslatableSnippet.PARSE(subtitle, locale()), fadeIn, stay, fadeOut);
    }

    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            handle(player);
        else {
            commandSender.sendMessage(BlobTranslatableSnippet.PARSE(title, locale()));
            commandSender.sendMessage(BlobTranslatableSnippet.PARSE(subtitle, locale()));
        }
    }

    @ApiStatus.Internal
    @Override
    public @NotNull BlobTitleMessage modify(Function<String, String> function) {
        return new BlobTitleMessage(identifier(), function.apply(title), function.apply(subtitle), fadeIn, stay,
                fadeOut, getSound(), locale());
    }

    @Override
    public @NotNull ModernMessage toModernMessage() {
        return new ModernMessage(identifier(), null, null, null, title, subtitle, fadeIn, stay, fadeOut, getSound(), locale(), getClickEvent());
    }

}
