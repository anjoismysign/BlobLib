package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public abstract class SerialBlobMessage implements BlobMessage {
    private final BlobSound sound;

    public SerialBlobMessage(BlobSound sound) {
        this.sound = sound;
    }

    public SerialBlobMessage() {
        sound = null;
    }

    @Override
    public abstract void send(Player player);

    @Override
    public void sendAndPlay(Player player) {
        send(player);
        if (sound != null)
            sound.play(player);
    }

    @Override
    public abstract void toCommandSender(CommandSender commandSender);

    @Override
    public BlobSound getSound() {
        return sound;
    }

    @Override
    public abstract SerialBlobMessage modify(Function<String, String> function);
}
