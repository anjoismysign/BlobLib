package us.mytheria.bloblib.entities.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BlobMessage {
    private final BlobSound sound;

    public BlobMessage(BlobSound sound) {
        this.sound = sound;
    }

    public BlobMessage() {
        sound = null;
    }

    public abstract void send(Player player);

    public void sendAndPlay(Player player) {
        send(player);
        if (sound != null)
            sound.play(player);
    }

    public abstract void toCommandSender(CommandSender commandSender);

    public BlobSound getSound() {
        return sound;
    }
}
