package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public interface PlayerAddress extends Address<Player> {

    static PlayerAddressBuilder builder() {
        return new PlayerAddressBuilder();
    }

    static PlayerAddress of(@NotNull String address) {
        Objects.requireNonNull(address, "'address' cannot be null");
        int length = address.length();
        if (length == 36) {
            UUID uuid = UUID.fromString(address);
            return new PlayerAddress() {
                @Override
                public @Nullable Player look() {
                    return Bukkit.getPlayer(uuid);
                }
            };
        }
        return new PlayerAddress() {
            @Override
            public @Nullable Player look() {
                return Bukkit.getPlayer(address);
            }
        };
    }

}
