package us.mytheria.bloblib.entities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerAddressBuilder {
    private @Nullable String name;
    private @Nullable UUID uuid;

    public PlayerAddress build(){
        if (name == null && uuid == null) {
            throw new IllegalStateException("Both name and UUID cannot be null");
        }
        return () -> {
            Player player;
            if (uuid != null){
                player = Bukkit.getPlayer(uuid);
            } else {
                player = Bukkit.getPlayer(name);
            }
            return player;
        };
    }

    public PlayerAddressBuilder setName(@Nullable String name) {
        this.name = name;
        return this;
    }

    public PlayerAddressBuilder setUuid(@Nullable UUID uuid) {
        this.uuid = uuid;
        return this;
    }
}
