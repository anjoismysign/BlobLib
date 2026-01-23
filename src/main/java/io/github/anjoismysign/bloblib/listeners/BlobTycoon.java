package io.github.anjoismysign.bloblib.listeners;

import io.github.anjoismysign.bloblib.BlobLib;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class BlobTycoon implements Listener {

    public BlobTycoon(){
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
    }

}
