package io.github.anjoismysign.bloblib.events;

import io.github.anjoismysign.bloblib.middleman.itemstack.OmniStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OmniStackUpdateEvent extends Event {

    @NotNull
    private final OmniStack omniStack;
    @Nullable
    private final Player player;
    private final boolean isMiniMessage;
    private @Nullable List<String> readLore;
    private @Nullable String readDisplayName;
    private @Nullable String readItemName;

    public OmniStackUpdateEvent(@NotNull OmniStack omniStack,
                                @Nullable Player player,
                                boolean isMiniMessage,
                                @Nullable List<String> readLore,
                                @Nullable String readDisplayName,
                                @Nullable String readItemName) {
        this.omniStack = omniStack;
        this.player = player;
        this.isMiniMessage = isMiniMessage;
        this.readLore = readLore;
        this.readDisplayName = readDisplayName;
        this.readItemName = readItemName;
    }

    public @NotNull OmniStack getOmniStack() {
        return omniStack;
    }

    public @Nullable Player getPlayer() {
        return player;
    }

    public boolean isMiniMessage() {
        return isMiniMessage;
    }

    public @Nullable List<String> getReadLore() {
        return readLore;
    }

    public void setReadLore(@Nullable List<String> readLore) {
        this.readLore = readLore;
    }

    public @Nullable String getReadDisplayName() {
        return readDisplayName;
    }

    public void setReadDisplayName(@Nullable String readDisplayName) {
        this.readDisplayName = readDisplayName;
    }

    public @Nullable String getReadItemName() {
        return readItemName;
    }

    public void setReadItemName(@Nullable String readItemName) {
        this.readItemName = readItemName;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
