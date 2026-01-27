package io.github.anjoismysign.bloblib.events;

import io.github.anjoismysign.bloblib.middleman.profile.Profile;
import io.github.anjoismysign.bloblib.middleman.profile.ProfileManagement;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called on main thread
 */
public class ProfileManagementQuitEvent extends Event {
    private final Player player;
    private final ProfileManagement management;
    private final Profile currentProfile;

    public ProfileManagementQuitEvent(@NotNull Player player,
                                      @NotNull ProfileManagement management,
                                      @NotNull Profile currentProfile,
                                      boolean isAsync){
        super(isAsync);
        this.player = player;
        this.management = management;
        this.currentProfile = currentProfile;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public ProfileManagement getProfileManagement() {
        return management;
    }

    @NotNull
    public Profile getCurrentProfile(){
        return currentProfile;
    }
}
