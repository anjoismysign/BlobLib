package us.mytheria.bloblib.entities.message;

import org.bukkit.entity.Player;

public class BlobTitle implements BlobMessage {
    private String title, subtitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    public BlobTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void send(Player player) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}
