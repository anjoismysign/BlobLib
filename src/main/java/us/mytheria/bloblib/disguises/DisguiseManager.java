package us.mytheria.bloblib.disguises;

import org.bukkit.Bukkit;

public class DisguiseManager {
    private boolean loaded;
    private Disguiser disguiser;

    public DisguiseManager() {
        loaded = false;
    }

    /**
     * Should not be run by plugins, only by BlobLib
     */
    public void load() {
        if (Bukkit.getPluginManager().getPlugin("LibsDisguises") != null) {
            updateDisguiser(new LibsDisguises());
        } else {
            updateDisguiser(new Absent());
        }
        loaded = true;
    }

    private void updateDisguiser(Disguiser disguiser) {
        this.disguiser = disguiser;
    }
    
    public Disguiser getDisguiser() {
        if (!loaded)
            throw new IllegalStateException("DisguiseManager loads after world load!");
        return this.disguiser;
    }
}
