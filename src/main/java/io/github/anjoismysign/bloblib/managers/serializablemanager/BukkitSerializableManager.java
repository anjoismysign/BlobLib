package io.github.anjoismysign.bloblib.managers.serializablemanager;

import io.github.anjoismysign.bloblib.entities.BlobScheduler;
import io.github.anjoismysign.bloblib.entities.PermissionDecorator;
import io.github.anjoismysign.bloblib.psa.BukkitDatabaseProvider;
import io.github.anjoismysign.psa.crud.CrudDatabaseCredentials;
import io.github.anjoismysign.psa.lehmapp.LehmappCrudable;
import io.github.anjoismysign.psa.lehmapp.LehmappSerializable;
import io.github.anjoismysign.psa.serializablemanager.SerializableManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public interface BukkitSerializableManager<T extends LehmappSerializable> extends SerializableManager<T> {

    @NotNull
    static  <T extends LehmappSerializable, S extends BukkitSerializableEvent<T>> BukkitSerializableManager<T> of(
            @NotNull Function<LehmappCrudable, T> deserializer,
            @Nullable Function<T, S> joinEvent,
            @Nullable Function<T, S> quitEvent,
            @Nullable Supplier<Boolean> eventsRegistrationSupplier,
            @NotNull JavaPlugin javaPlugin) {
        Objects.requireNonNull(deserializer, "'deserializeFunction' cannot be null");
        CrudDatabaseCredentials crudDatabaseCredentials = BukkitDatabaseProvider.INSTANCE.getDatabaseProvider().of(javaPlugin);
        return new AbstractBukkitSerializableManager<>(
                crudDatabaseCredentials,
                deserializer,
                joinEvent,
                quitEvent,
                javaPlugin,
                eventsRegistrationSupplier) {
        };
    }

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
