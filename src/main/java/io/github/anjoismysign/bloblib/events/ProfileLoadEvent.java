package io.github.anjoismysign.bloblib.events;

import io.github.anjoismysign.bloblib.middleman.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ProfileLoadEvent extends Event {
    private final Player player;
    private final Profile profile;

    public ProfileLoadEvent(@NotNull Player player,
                            @NotNull Profile profile,
                            boolean isAsync){
        super(isAsync);
        this.player = player;
        this.profile = profile;
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
    public Profile getProfile() {
        return profile;
    }
}
