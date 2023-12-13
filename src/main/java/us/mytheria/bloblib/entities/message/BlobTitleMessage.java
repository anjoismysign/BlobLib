package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.entities.translatable.BlobTranslatableSnippet;

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
        this.title = BlobTranslatableSnippet.PARSE(title, locale);
        this.subtitle = BlobTranslatableSnippet.PARSE(subtitle, locale);
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void send(Player player) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void toCommandSender(CommandSender commandSender) {
        if (commandSender instanceof Player player)
            handle(player);
        else {
            commandSender.sendMessage(title);
            commandSender.sendMessage(subtitle);
        }
    }

    @Override
    public @NotNull BlobTitleMessage modify(Function<String, String> function) {
        return new BlobTitleMessage(getReference(), function.apply(title), function.apply(subtitle), fadeIn, stay,
                fadeOut, getSound(), getLocale());
    }
}
