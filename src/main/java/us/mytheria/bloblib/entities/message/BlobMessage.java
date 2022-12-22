package us.mytheria.bloblib.entities.message;

import org.bukkit.entity.Player;

public abstract class BlobMessage {
    private BlobSound sound;

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

    public BlobSound getSound() {
        return sound;
    }
}
