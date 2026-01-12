package io.github.anjoismysign.bloblib.listeners;

import io.github.anjoismysign.alternativesaving.entity.SerialPlayer;
import io.github.anjoismysign.alternativesaving.event.SerialPlayerJoinEvent;
import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibProfileAPI;
import io.github.anjoismysign.bloblib.events.ProfileLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlobTycoon implements Listener {

    public BlobTycoon(){
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
    }

    @EventHandler
    public void onJoin(SerialPlayerJoinEvent event) {
        SerialPlayer serialPlayer = event.getSerialPlayer();
        Player player = serialPlayer.getPlayer();
        if (player == null){
            return;
        }
        var provider = BlobLibProfileAPI.getInstance().getProvider();
        if (!provider.isValid()){
            return;
        }
        var profileManagement = provider.getProfileManagement(player);
        var profile = profileManagement.getProfiles().get(profileManagement.getCurrentProfileIndex());
        ProfileLoadEvent loadEvent = new ProfileLoadEvent(player, profile, false);
        Bukkit.getPluginManager().callEvent(loadEvent);
    }
}
