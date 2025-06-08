package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public interface PlayerAddress extends Address<Player> {

    static PlayerAddress of(@NotNull String address) {
        Objects.requireNonNull(address, "'address' cannot be null");
        int length = address.length();
        Builder builder = builder();
        if (length == 36) {
            UUID uuid = UUID.fromString(address);
            return builder.setUUID(uuid).build();
        }
        return builder.setName(address).build();
    }

    static Builder builder() {
        return new Builder();
    }

    class Builder {
        private @Nullable String name;
        private @Nullable UUID uuid;

        public PlayerAddress build() {
            if (name == null && uuid == null) {
                throw new IllegalStateException("Both name and UUID cannot be null");
            }
            return () -> {
                Player player;
                if (uuid != null) {
                    player = Bukkit.getPlayer(uuid);
                } else {
                    player = Bukkit.getPlayer(name);
                }
                return player;
            };
        }

        public Builder setName(@Nullable String name) {
            this.name = name;
            return this;
        }

        public Builder setUUID(@Nullable UUID uuid) {
            this.uuid = uuid;
            return this;
        }
    }

}
