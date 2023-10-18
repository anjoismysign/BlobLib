package us.mytheria.bloblib.entities.message;

import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class SerialBlobMessage implements BlobMessage {
    @Nullable
    private final BlobSound sound;
    @NotNull
    private final String locale;
    @Nullable
    private final ClickEvent clickEvent;

    public SerialBlobMessage(@Nullable BlobSound sound,
                             @NotNull String locale,
                             @Nullable ClickEvent clickEvent) {
        this.sound = sound;
        this.locale = locale;
        this.clickEvent = clickEvent;
    }

    public SerialBlobMessage() {
        sound = null;
        locale = "en_us";
        clickEvent = null;
    }

    @Override
    public abstract void send(Player player);

    @Override
    public abstract void toCommandSender(CommandSender commandSender);

    /**
     * Will retrieve the BlobSound object.
     *
     * @return The sound to play
     */
    @Override
    public BlobSound getSound() {
        return sound;
    }

    /**
     * Will retrieve the locale of the message.
     *
     * @return The locale of the message
     */
    @Override
    @NotNull
    public String getLocale() {
        return locale;
    }

    @Nullable
    @Override
    public ClickEvent getClickEvent() {
        return clickEvent;
    }

    @NotNull
    public SerialBlobMessage onClick(ClickEvent event) {
        return this;
    }

    @Override
    public abstract @NotNull SerialBlobMessage modify(Function<String, String> function);
}
