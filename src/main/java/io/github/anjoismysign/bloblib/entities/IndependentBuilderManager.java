package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.bloblib.entities.inventory.BlobObjectBuilder;
import io.github.anjoismysign.bloblib.managers.ManagerDirector;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;

public class IndependentBuilderManager<T extends BlobObject> extends BuilderManager<T, BlobObjectBuilder<T>> {

    private Function<UUID, BlobObjectBuilder<T>> function;

    public IndependentBuilderManager(ManagerDirector managerDirector,
                                     String fileKey) {
        super(managerDirector, fileKey);
    }

    @Override
    @NotNull
    public BlobObjectBuilder<T> getOrDefault(UUID uuid) {
        BlobObjectBuilder<T> objectBuilder = this.builders.get(uuid);
        if (objectBuilder == null) {
            objectBuilder = function.apply(uuid);
            builders.put(uuid, objectBuilder);
            inventories.put(objectBuilder.getInventory(), objectBuilder);
        }
        return objectBuilder;
    }

    @Override
    @NotNull
    public IndependentBuilderManager<T> addBuilder(UUID uuid, BlobObjectBuilder<T> builder) {
        super.addBuilder(uuid, builder);
        return this;
    }

    @Override
    @NotNull
    public IndependentBuilderManager<T> addBuilder(Player player, BlobObjectBuilder<T> builder) {
        super.addBuilder(player, builder);
        return this;
    }

    @Override
    @NotNull
    public IndependentBuilderManager<T> removeBuilder(UUID uuid) {
        super.removeBuilder(uuid);
        return this;
    }

    @Override
    @NotNull
    public IndependentBuilderManager<T> removeBuilder(Player player) {
        super.removeBuilder(player);
        return this;
    }

    @NotNull
    public IndependentBuilderManager<T> setBuilderFunction(Function<UUID, BlobObjectBuilder<T>> function) {
        this.function = function;
        return this;
    }
}