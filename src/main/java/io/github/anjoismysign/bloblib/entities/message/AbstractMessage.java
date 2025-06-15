package io.github.anjoismysign.bloblib.entities.message;

import io.github.anjoismysign.bloblib.entities.DataAssetType;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class AbstractMessage implements BlobMessage {
    @NotNull
    private final String reference;
    @Nullable
    private final BlobSound sound;
    @NotNull
    private final String locale;
    @Nullable
    private final ClickEvent clickEvent;

    public AbstractMessage(@NotNull String reference,
                           @Nullable BlobSound sound,
                           @NotNull String locale,
                           @Nullable ClickEvent clickEvent) {
        this.reference = reference;
        this.sound = sound;
        this.locale = locale;
        this.clickEvent = clickEvent;
    }

    public abstract void send(Player player);

    public abstract void toCommandSender(CommandSender commandSender);

    public String identifier() {
        return reference;
    }

    public DataAssetType getType() {
        return DataAssetType.BLOB_MESSAGE;
    }

    /**
     * Will retrieve the BlobSound object.
     *
     * @return The sound to play
     */
    public BlobSound getSound() {
        return sound;
    }

    /**
     * Will retrieve the locale of the message.
     *
     * @return The locale of the message
     */
    @NotNull
    public String locale() {
        return locale;
    }

    @Nullable
    public ClickEvent getClickEvent() {
        return clickEvent;
    }

    @NotNull
    public AbstractMessage onClick(ClickEvent event) {
        return this;
    }

    public abstract @NotNull AbstractMessage modify(Function<String, String> function);
}
