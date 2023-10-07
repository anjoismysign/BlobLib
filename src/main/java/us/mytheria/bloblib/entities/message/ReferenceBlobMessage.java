package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ReferenceBlobMessage implements BlobMessage {
    private final SerialBlobMessage message;
    private final String reference;

    public ReferenceBlobMessage(SerialBlobMessage message, String reference) {
        this.message = message;
        this.reference = reference;
    }

    @Override
    public void send(Player player) {
        message.send(player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void sendAndPlay(Player player) {
        message.sendAndPlay(player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void sendAndPlayInWorld(Player player) {
        message.sendAndPlayInWorld(player);
    }

    @Override
    public void toCommandSender(CommandSender commandSender) {
        message.toCommandSender(commandSender);
    }

    @Override
    public BlobSound getSound() {
        return message.getSound();
    }

    @Override
    public @NotNull ReferenceBlobMessage modify(Function<String, String> function) {
        return new ReferenceBlobMessage(message.modify(function), reference);
    }

    public String getReference() {
        return reference;
    }

    /**
     * Will retrieve the locale of the message.
     *
     * @return The locale of the message
     */
    @Override
    @NotNull
    public String getLocale() {
        return message.getLocale();
    }
}
