package io.github.anjoismysign.bloblib.api;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.entities.message.BlobSound;
import io.github.anjoismysign.bloblib.managers.DataAssetManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class BlobLibSoundAPI {
    private static BlobLibSoundAPI instance;
    private final BlobLib plugin;

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

    private BlobLibSoundAPI(BlobLib plugin) {
        this.plugin = plugin;
    }

    /**
     * @return The sound manager
     */
    public DataAssetManager<BlobSound> getSoundManager() {
        return plugin.getSoundManager();
    }

    /**
     * @param key The key of the sound
     * @return The sound
     */
    @Nullable
    public BlobSound getSound(String key) {
        return getSoundManager().getAsset(key);
    }

    /**
     * @param key    The key of the sound
     * @param player The player to play the sound
     */
    public void playSound(String key, Player player) {
        BlobSound sound = getSoundManager().getAsset(key);
        if (sound == null)
            throw new NullPointerException("Sound '" + key + "' does not exist!");
        sound.play(player);
    }

    public List<BlobSound> getDefault() {
        return getSoundManager().getAssets();
    }

    public Map<String, BlobSound> mapDefault(){
        return getSoundManager().mapAssets();
    }
}
