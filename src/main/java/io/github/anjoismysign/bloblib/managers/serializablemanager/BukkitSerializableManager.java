package io.github.anjoismysign.bloblib.managers.serializablemanager;

import io.github.anjoismysign.bloblib.entities.BlobScheduler;
import io.github.anjoismysign.bloblib.entities.PermissionDecorator;
import io.github.anjoismysign.psa.lehmapp.LehmappSerializable;
import io.github.anjoismysign.psa.serializablemanager.SerializableManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface BukkitSerializableManager<T extends LehmappSerializable> extends SerializableManager<T> {

    @NotNull
    BlobScheduler getScheduler();

    @NotNull
    PermissionDecorator getPermissionDecorator();

    void unregisterEvents();

    /**
     * Looks if there's a serializable tied to a specific player
     *
     * @param player the player that the serializable would be tied to
     * @return the serializable in case of being cached, null otherwise
     */
    @Nullable
    T lookFor(@NotNull Player player);

    /**
     * Looks if there's a serializable tied to a specific player.
     *
     * @param player the player that the serializable would be tied to
     * @return the serializable in case of being cached
     * @throws NullPointerException if player is not cached
     */
    @NotNull
    default T get(@NotNull Player player) {
        return Objects.requireNonNull(lookFor(player), player.getName() + " doesn't seem to be cached");
    }

}
