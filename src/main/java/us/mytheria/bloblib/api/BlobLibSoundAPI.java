package us.mytheria.bloblib.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import us.mytheria.bloblib.BlobLib;
import us.mytheria.bloblib.entities.message.BlobSound;
import us.mytheria.bloblib.managers.SoundManager;

public class BlobLibSoundAPI {
    private static BlobLibSoundAPI instance;
    private final BlobLib plugin;

    private BlobLibSoundAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    public static BlobLibSoundAPI getInstance(BlobLib plugin) {
        if (instance == null) {
            if (plugin == null)
                throw new NullPointerException("injected dependency is null");
            BlobLibSoundAPI.instance = new BlobLibSoundAPI(plugin);
        }
        return instance;
    }

    public static BlobLibSoundAPI getInstance() {
        return getInstance(null);
    }

    /**
     * @return The sound manager
     */
    public SoundManager getSoundManager() {
        return plugin.getSoundManager();
    }

    /**
     * @param key The key of the sound
     * @return The sound
     */
    @Nullable
    public BlobSound getSound(String key) {
        return getSoundManager().getSound(key);
    }

    /**
     * @param key    The key of the sound
     * @param player The player to play the sound
     */
    public void playSound(String key, Player player) {
        getSoundManager().play(player, key);
    }
}
