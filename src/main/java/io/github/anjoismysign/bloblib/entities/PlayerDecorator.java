package io.github.anjoismysign.bloblib.entities;

import io.papermc.paper.connection.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class PlayerDecorator {
    private final @NotNull PlayerConnection connection;
    private final @NotNull UUID uniqueId;

    private PlayerDecorator(@NotNull PlayerConnection connection,
                           @NotNull UUID uniqueId) {
        this.connection = connection;
        this.uniqueId = uniqueId;
    }

    public static PlayerDecorator of(@NotNull Player player) {
        UUID uniqueId = player.getUniqueId();
        return new PlayerDecorator(player.getConnection(), uniqueId);
    }

    @NotNull
    public PermissibleDecorator getPermissible() {
        Player lookup = Bukkit.getPlayer(uniqueId);
        if (lookup == null)
            throw new NullPointerException("'lookup' is null");
        return PermissibleDecorator.of(AddressFactory.getInstance().of(lookup));
    }

    public boolean isValid() {
        return connection.isConnected();
    }

    @Nullable
    public Player lookup(){
        return isValid() ? Bukkit.getPlayer(uniqueId) : null;
    }

    public @NotNull UUID getUniqueId() {
        return uniqueId;
    }

}
