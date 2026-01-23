package io.github.anjoismysign.bloblib.listeners;

import io.github.anjoismysign.alternativesaving.entity.SerialPlayer;
import io.github.anjoismysign.alternativesaving.entity.SerialProfile;
import io.github.anjoismysign.alternativesaving.event.SerialPlayerJoinEvent;
import io.github.anjoismysign.alternativesaving.event.SerialPlayerQuitEvent;
import io.github.anjoismysign.alternativesaving.event.SerialProfileLoadEvent;
import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.bloblib.api.BlobLibProfileAPI;
import io.github.anjoismysign.bloblib.events.ProfileLoadEvent;
import io.github.anjoismysign.bloblib.events.ProfileManagementQuitEvent;
import io.github.anjoismysign.bloblib.middleman.AlternativeSavingMiddleman;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AlternativeSaving implements Listener {

    public AlternativeSaving(){
        Bukkit.getPluginManager().registerEvents(this, BlobLib.getInstance());
    }

    @EventHandler
    public void onJoin(SerialPlayerJoinEvent event){
        SerialPlayer serialPlayer = event.getSerialPlayer();
        Player player = serialPlayer.getPlayer();
        if (player == null){
            BlobLib.getInstance().getLogger().info("SerialPlayerJoinEvent: player == null");
            return;
        }
        SerialProfile serialProfile = serialPlayer.getProfiles().get(serialPlayer.getSelectedProfile());
        AlternativeSavingMiddleman.ASProfile profile = new AlternativeSavingMiddleman.ASProfile(serialPlayer, serialProfile);
        ProfileLoadEvent loadEvent = new ProfileLoadEvent(player, profile, false);
        Bukkit.getPluginManager().callEvent(loadEvent);
    }

    @EventHandler
    public void onLoad(SerialProfileLoadEvent event) {
        Player player = event.getPlayer();
        SerialPlayer serialPlayer = event.getSerialPlayer();
        SerialProfile serialProfile = event.getSerialProfile();
        AlternativeSavingMiddleman.ASProfile profile = new AlternativeSavingMiddleman.ASProfile(serialPlayer, serialProfile);
        ProfileLoadEvent loadEvent = new ProfileLoadEvent(player, profile, false);
        Bukkit.getPluginManager().callEvent(loadEvent);
    }

    @EventHandler
    public void onQuit(SerialPlayerQuitEvent event){
        SerialPlayer serialPlayer = event.getSerialPlayer();
        Player player = serialPlayer.getPlayer();
        if (player == null){
            BlobLib.getInstance().getLogger().info("SerialPlayerJoinEvent: player == null");
            return;
        }
        SerialProfile serialProfile = serialPlayer.getProfiles().get(serialPlayer.getSelectedProfile());
        AlternativeSavingMiddleman.ASProfile profile = new AlternativeSavingMiddleman.ASProfile(serialPlayer, serialProfile);
        AlternativeSavingMiddleman.ASProfileManagement management = new AlternativeSavingMiddleman.ASProfileManagement(serialPlayer);
        ProfileManagementQuitEvent quitEvent = new ProfileManagementQuitEvent(player, management, profile, false);
        Bukkit.getPluginManager().callEvent(quitEvent);
    }
}
