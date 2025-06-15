package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.bloblib.entities.inventory.ObjectBuilder;
import io.github.anjoismysign.bloblib.managers.ManagerDirector;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.BiFunction;

public class ObjectBuilderManager<T extends BlobObject> extends BuilderManager<T, ObjectBuilder<T>> {

    private BiFunction<UUID, ObjectDirector<T>, ObjectBuilder<T>> builderBiFunction;
    private final ObjectDirector<T> objectDirector;

    public ObjectBuilderManager(ManagerDirector managerDirector,
                                String fileKey, ObjectDirector<T> objectDirector) {
        super(managerDirector, fileKey);
        this.objectDirector = objectDirector;
    }

    @NotNull
    public ObjectDirector<T> getObjectDirector() {
        return objectDirector;
    }

    @Override
    @NotNull
    public ObjectBuilder<T> getOrDefault(UUID uuid) {
        ObjectBuilder<T> objectBuilder = builders.get(uuid);
        if (objectBuilder == null) {
            objectBuilder = builderBiFunction.apply(uuid, getObjectDirector());
            builders.put(uuid, objectBuilder);
            inventories.put(objectBuilder.getInventory(), objectBuilder);
        }
        return objectBuilder;
    }

    @Override
    @NotNull
    public ObjectBuilderManager<T> addBuilder(UUID uuid, ObjectBuilder<T> builder) {
        super.addBuilder(uuid, builder);
        return this;
    }

    @Override
    @NotNull
    public ObjectBuilderManager<T> addBuilder(Player player, ObjectBuilder<T> builder) {
        super.addBuilder(player, builder);
        return this;
    }

    @Override
    @NotNull
    public ObjectBuilderManager<T> removeBuilder(UUID uuid) {
        super.removeBuilder(uuid);
        return this;
    }

    @Override
    @NotNull
    public ObjectBuilderManager<T> removeBuilder(Player player) {
        super.removeBuilder(player);
        return this;
    }

    @NotNull
    public ObjectBuilderManager<T> setBuilderBiFunction(BiFunction<UUID, ObjectDirector<T>, ObjectBuilder<T>> function) {
        builderBiFunction = function;
        return this;
    }

}