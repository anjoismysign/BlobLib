package io.github.anjoismysign.bloblib.entities.loot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record LootEntry(@NotNull LootEntryType type,
                        @Nullable String name,
                        int weight,
                        @Nullable List<LootFunction> functions) {

    public LootEntry {
        if (weight <= 0)
            weight = 1;
    }
}
