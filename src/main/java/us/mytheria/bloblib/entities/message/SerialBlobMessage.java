package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class SerialBlobMessage implements BlobMessage {
    private final BlobSound sound;
    private final String locale;

    public SerialBlobMessage(BlobSound sound, String locale) {
        this.sound = sound;
        this.locale = locale;
    }

    public SerialBlobMessage() {
        sound = null;
        locale = null;
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
    @Nullable
    public String getLocale() {
        return locale;
    }

    @Override
    public abstract @NotNull SerialBlobMessage modify(Function<String, String> function);
}
