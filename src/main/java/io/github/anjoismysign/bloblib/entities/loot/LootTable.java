package io.github.anjoismysign.bloblib.entities.loot;

import io.github.anjoismysign.bloblib.BlobLib;
import io.github.anjoismysign.holoworld.asset.DataAsset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public record LootTable(@NotNull List<LootPool> pools,
                        @NotNull String identifier) implements DataAsset {

    @Nullable
    public static LootTable by(@NotNull String key) {
        Objects.requireNonNull(key);
        return BlobLib.getInstance().getLootTableManager().getLootTable(key);
    }
}
